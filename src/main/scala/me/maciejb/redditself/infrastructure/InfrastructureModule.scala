package me.maciejb.redditself.infrastructure

import scala.concurrent.ExecutionContext


trait InfrastructureModule {
  implicit val ec = ExecutionContext.Implicits.global
}