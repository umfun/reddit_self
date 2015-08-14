package me.maciejb.redditself.client2

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializerSettings, ActorMaterializer}
import me.maciejb.redditself.commons.Redditers

import scala.concurrent.Await

import scala.concurrent.duration._

import me.maciejb.redditself.commons.StreamsShutdown._

object CommentClientApp extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  val fut = new CommentsClient().recentCommentsOf(Redditers.WayFairer)
  fut.shutdownOnComplete()

  val result = Await.result(fut, 10.seconds)

  println(result)

}
