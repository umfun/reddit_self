package me.maciejb.redditself.client2

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import me.maciejb.redditself.commons.Redditers
import me.maciejb.redditself.model.Comment

object RedditCommentsApp extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  val counter = new AtomicInteger(0)

  val allCommentsSink = Sink.foreach[Comment] { comment =>
    val curCounter = counter.incrementAndGet()
    println(s"Retrieved $curCounter comments.")
  }

  private val runnableGraph = Source.single(Redditers.WayFairer).via(new RedditComments().all).to(allCommentsSink)

  materializer.materialize(runnableGraph)

}
