package me.maciejb.redditself.client2

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Source, Sink, Flow}
import me.maciejb.redditself.domain.{Comment, Listing, Username}
import org.json4s.JsonAST.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class CommentsClient()(implicit system: ActorSystem,
                       mat: Materializer,
                       ec: ExecutionContext) extends BaseRedditApiClient {

  import CommentsClient._

  private def recentCommentsReq(username: Username) =
    HttpRequest(HttpMethods.GET, uri = s"/user/${username.value}/comments.json")

  private val unmarshalCommentsFlow: Flow[(Try[HttpResponse], RequestContext), Comment, Unit] = {
    Flow[(Try[HttpResponse], RequestContext)].mapAsync(UnmarshallingParallelism) { case (triedResp, context) =>
      triedResp match {
        case Success(resp) =>
          resp.status match {
            case StatusCodes.OK =>
              Unmarshal(resp.entity).to[JValue].map(Listing.extractComments)
            case _ =>
              val error = s"RedditAPI request failed with status code ${resp.status} and entity ${resp.entity}."
              Future.failed(new IOException(error))
          }
      }
    }.mapConcat(_.children)
  }

  private val foldComments: Flow[Comment, List[Comment], Unit] =
    Flow[Comment].fold(List[Comment]())((acc, comment) => comment :: acc)

  val recentCommentsFlow: Flow[Username, Comment, Unit] = {
    val respFlow = Flow[Username].map(recentCommentsReq(_) -> RequestContext.default).via(RedditApi.connectionPool)
    respFlow.via(unmarshalCommentsFlow)
  }

  def recentComments(username: Username): Future[List[Comment]] = {
    Source.single(username).via(recentCommentsFlow).via(foldComments).runWith(Sink.head)
  }

}

object CommentsClient {
  val UnmarshallingParallelism = 1
}