package me.maciejb.redditself.redditapi.internal

import me.maciejb.redditself.model.Fullname

private[redditapi] case class QueryParam(name: String, value: String)

private[redditapi] object QueryParam {
  def fromFullname(name: String, valueOpt: Option[Fullname]): Option[QueryParam] =
    for (value <- valueOpt) yield {
      QueryParam(name, value.v)
    }

  def toDispatch(seq: Seq[Option[QueryParam]]): Traversable[(String, String)] =
    seq.flatten.map { case QueryParam(k, v) => (k, v) }

}
