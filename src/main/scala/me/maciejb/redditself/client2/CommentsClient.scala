package me.maciejb.redditself.client2

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import me.maciejb.redditself.redditapi.domain.{Listing, Comment, Username}
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class RequestContext(id: String = "nothing yet")

object RequestContext {
  val default = new RequestContext()
}

private[client2] object RedditApi {
  type RedditApiFlow = Flow[(HttpRequest, RequestContext), (Try[HttpResponse], RequestContext), Http.HostConnectionPool]
  def connectionPool(implicit system: ActorSystem, mat: Materializer): RedditApiFlow =
    Http().cachedHostConnectionPool[RequestContext]("www.reddit.com")

  def request(httpRequest: HttpRequest)(implicit system: ActorSystem,
                                        mat: Materializer,
                                        ec: ExecutionContext): Future[HttpResponse] =
    Source.single(httpRequest -> RequestContext.default).via(connectionPool).runWith(Sink.head)
      .map(_._1).flatMap(Future.fromTry)
}

class CommentsClient()(implicit system: ActorSystem,
                       mat: Materializer,
                       ec: ExecutionContext) {


  import Json4sSupport._

  implicit val serialization = org.json4s.jackson.Serialization
  implicit val shouldWritePretty = ShouldWritePretty.True
  implicit val json4sFormat = DefaultFormats

  private def recentCommentsReq(username: Username) =
    HttpRequest(HttpMethods.GET, uri = s"/user/${username.value}/comments.json")

  def recentCommentsFlow: Flow[Username, Try[List[Comment]], _] = ???

  def recentCommentsOf(username: Username): Future[Listing[Comment]] =
    RedditApi.request(recentCommentsReq(username)).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[JValue].map(Listing.extractComments)
        case StatusCodes.BadRequest => Future.failed(new RuntimeException)
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"RedditAPI request failed with status code ${response.status} and entity $entity"
          Future.failed(new IOException(error))
        }
      }
    }

}


case class LimitedEntity(kind: String)
