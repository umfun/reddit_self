package me.maciejb.redditself.redditapi.dtos

import org.json4s.JsonAST.{JArray, JField, JValue}
import org.json4s.jackson.JsonMethods._

case class Listing[T](children: List[T], after: Option[String], before: Option[String])

object Listing {

  import me.maciejb.redditself.redditapi.json4sFormats

  def extractComments(str: String): Listing[Comment] = extractComments(parse(str))
  def extractComments(jVal: JValue): Listing[Comment] = prepareForExtraction(jVal).extract[Listing[Comment]]

  private def prepareForExtraction(jVal: JValue): JValue = {
    (jVal \ "data").transformField {
      case JField("children", children) =>
        JField("children", JArray(children.children.map(_ \ "data")))
    }
  }

}
