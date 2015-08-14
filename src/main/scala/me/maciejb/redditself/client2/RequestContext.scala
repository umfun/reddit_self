package me.maciejb.redditself.client2

case class RequestContext(id: String = "nothing yet")

object RequestContext {
  val default = new RequestContext()
}
