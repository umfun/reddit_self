package me.maciejb.redditself

package object client2 {

  private[client2] implicit class OptionalParamsExt(optionalParams: Seq[(String, Option[String])]) {
    def compacted = optionalParams.foldLeft(List[(String, String)]()) {
      case (acc, (k, Some(v))) => (k, v) :: acc
      case (acc, (_, None)) => acc
    }
  }

}
