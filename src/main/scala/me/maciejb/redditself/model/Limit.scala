package me.maciejb.redditself.model


case class Limit(value: Int) extends AnyVal {

  import Limit._

  def asQueryParam: (String, String) = ("limit", value.toString)

  def isValid = value > 0 && value <= Max.value
}

object Limit {
  val Max = Limit(100)
  val Default = Limit(25)
}
