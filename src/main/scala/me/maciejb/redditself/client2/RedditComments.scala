package me.maciejb.redditself.client2

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl._
import me.maciejb.redditself.model._
import org.json4s.JsonAST.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class RedditComments()(implicit system: ActorSystem,
                       mat: Materializer,
                       ec: ExecutionContext) extends BaseRedditApiClient {

  import RedditComments._

  private val connectionPool = RedditApi.connectionPool[Request]

  private lazy val unmarshalCommentsListingFlow: Flow[(Try[HttpResponse], Request), (Listing[Comment], Request), Unit] =
    Flow[(Try[HttpResponse], Request)].mapAsync(UnmarshallingParallelism) { case (triedResp, request) =>
      triedResp match {
        case Success(resp) =>
          resp.status match {
            case StatusCodes.OK =>
              Unmarshal(resp.entity).to[JValue].map(jv => (Listing.extractComments(jv), request))
            case _ =>
              val error = s"RedditAPI request failed with status code ${resp.status} and entity ${resp.entity}."
              Future.failed(new IOException(error))
          }
        case Failure(e) => sys.error(s"RedditAPI request failed.")
      }
    }

  lazy val all: Flow[Username, Comment, Unit] = Flow() { implicit b =>
    import FlowGraph.Implicits._

    val inFlow = b.add(Flow[Username].map(Request(_)))
    val reqFlow = b.add(reqRespFlow)
    val mergeReqs = b.add(Merge[Request](2))

    val splitListing =
      b.add(UnzipWith[(Listing[Comment], Request), List[Comment], (Option[Fullname], Request)]
        (Listing.forwardUnzipper))

    val nextPage = b.add {
      Flow[(Option[Fullname], Request)].collect { case (after@Some(_), prevReq) => prevReq.withAfter(after) }
    }

    val concatComments = b.add {
      Flow[List[Comment]].mapConcat(identity)
    }

    // @formatter:off
    inFlow    ~>    mergeReqs
    nextPage  ~>    mergeReqs
                    mergeReqs   ~>   reqFlow  ~> splitListing.in
                                                 splitListing.out0 ~> concatComments
                                                 splitListing.out1 ~> nextPage
    // @formatter:on

    (inFlow.inlet, concatComments.outlet)
  }


  private lazy val foldComments: Flow[Comment, List[Comment], Unit] =
    Flow[Comment].fold(List[Comment]())((acc, comment) => comment :: acc)

  lazy val reqRespFlow: Flow[Request, (Listing[Comment], Request), Unit] = Flow[Request].map(_.toRequestWithContext)
    .via(connectionPool)
    .via(unmarshalCommentsListingFlow)

  lazy val recentCommentsFlow: Flow[Username, Comment, Unit] =
    Flow[Username].map(Request(_)).via(reqRespFlow).map(_._1).mapConcat(_.children)

  def recentComments(username: Username): Future[List[Comment]] = {
    Source.single(username).via(recentCommentsFlow).via(foldComments).runWith(Sink.head)
  }

}

object RedditComments {
  private[RedditComments] val UnmarshallingParallelism = 1

  case class Request(username: Username,
                     limit: Limit = Limit.Default,
                     after: Option[Fullname] = None) extends RedditRequestContext {

    def toHttpRequest: HttpRequest = {
      val mandatoryParams = Seq(limit.asQueryParam)
      val optionalParams = Seq("after" -> after.map(_.v))

      val query = Map(mandatoryParams ++ optionalParams.compacted: _*)

      Uri(s"/user/${username.value}/comments.json").withQuery(query)
      HttpRequest(HttpMethods.GET, uri = s"/user/${username.value}/comments.json")
    }

    private[client2] def toRequestWithContext: (HttpRequest, Request) = toHttpRequest -> this

    def withAfter(after: Option[Fullname]) = copy(after = after)

  }
}