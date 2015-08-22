package me.maciejb.redditself.model

import java.time.LocalDateTime

import com.softwaremill.thegarden.json4s.serializers.CamelCaseFieldNameDeserializer
import org.json4s._

/**
 * @author Maciej Bilas
 * @since 6/4/15 16:49
 */
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
