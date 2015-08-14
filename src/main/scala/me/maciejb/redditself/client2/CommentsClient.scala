package me.maciejb.redditself.client2

import java.io.IOException

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import me.maciejb.redditself.domain.{Listing, Comment, Username}
import org.json4s.JsonAST.JValue

import scala.concurrent.{ExecutionContext, Future}

class CommentsClient()(implicit system: ActorSystem,
                       mat: Materializer,
                       ec: ExecutionContext) extends BaseRedditApiClient {

  private def recentCommentsReq(username: Username) =
    HttpRequest(HttpMethods.GET, uri = s"/user/${username.value}/comments.json")

  def recentCommentsFlow: Flow[Username, Comment, _] = ???

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
