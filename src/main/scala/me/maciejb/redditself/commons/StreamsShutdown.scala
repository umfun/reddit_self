package me.maciejb.redditself.commons

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}


object StreamsShutdown {
  implicit class StreamSutureExt(fut: Future[_]) {
    def shutdownOnComplete()
                          (implicit ec: ExecutionContext,
                           system: ActorSystem,
                           materializer: ActorMaterializer) =
      fut onComplete { _ =>
        materializer.shutdown()
        system.shutdown()
        system.awaitTermination()
      }


  }
}
