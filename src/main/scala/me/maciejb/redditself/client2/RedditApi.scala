package me.maciejb.redditself.client2

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

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
