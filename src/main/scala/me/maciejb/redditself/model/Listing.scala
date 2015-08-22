package me.maciejb.redditself.model

import org.json4s.JsonAST.{JArray, JField, JValue}
import org.json4s.jackson.JsonMethods._

case class Listing[T](children: List[T], after: Option[String], before: Option[String]) {
  def beforeFN = before.map(Fullname)
  def afterFN = after.map(Fullname)

}

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

  /* type madness! */
  def forwardUnzipper[T, Context]: (((Listing[T], Context)) => (List[T], (Option[Fullname], Context))) = {
    case (listing, context) => (listing.children, (listing.afterFN, context))
  }

}
