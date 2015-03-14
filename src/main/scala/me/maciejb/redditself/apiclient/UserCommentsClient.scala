package me.maciejb.redditself.apiclient

import java.time.LocalDateTime

import com.softwaremill.thegarden.json4s.serializers.CamelCaseFieldNameDeserializer
import dispatch.Defaults._
import dispatch._
import me.maciejb.redditself.Username
import org.json4s._
import org.json4s.jackson.JsonMethods._

class UserCommentsClient(user: Username) {
  val commentsUri = url(s"https://www.reddit.com/user/${user.value}/comments.json")

  def commentsJsonStr: Future[String] = Http(commentsUri OK as.String)

  def comments: Future[List[Comment]] = for (str <- commentsJsonStr) yield {
    (parse(str) \ "data" \ "children" \ "data").extract[List[Comment]]
  }

}

case class Comment(id: String, archived: Boolean, author: Username,
                   body: String, bodyHtml: String,
                   createdAt: LocalDateTime, linkUrl: String, score: Int)

object Comment {

  private val deserializeDate: PartialFunction[JField, JField] = {
    case JField("created_utc", JDouble(v)) => JField("createdAt", JString(v.formatted("%.0f")))
  }

  val serializer = FieldSerializer[Comment](
    deserializer = deserializeDate orElse CamelCaseFieldNameDeserializer.deserializer
  )

}
