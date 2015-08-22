package me.maciejb.redditself.client2

private[client2] case class EmptyContext(id: String = "nothing yet") extends RedditRequestContext

private[client2] object EmptyContext {
  val default = new EmptyContext()
}
