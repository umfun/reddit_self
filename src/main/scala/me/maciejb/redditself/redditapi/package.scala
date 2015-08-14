package me.maciejb.redditself

import java.util.Date

import com.softwaremill.thegarden.json4s.serializers.{StandardizeFieldNames, JavaTimeSerializers}
import me.maciejb.redditself.redditapi.domain.Comment
import org.json4s._

import scala.util.control.NonFatal

/**
 * @author Maciej Bilas
 * @since 14/3/15 16:34
 */
package object redditapi {

  implicit val json4sFormats: Formats = {
    val base = new DefaultFormats {

      override val dateFormat: DateFormat = new DateFormat {
        override def parse(s: String) = try {
          Some(new Date(s.toLong * 1000))
        } catch {
          case NonFatal(_) => None
        }

        override def format(d: Date) = d.toInstant.toEpochMilli.toString

      }

    }
    val withExt = base ++ JavaTimeSerializers.all + StandardizeFieldNames

    val entitySerializers = List(Comment.serializer)

    entitySerializers.foldLeft(withExt) { (formats, serializer) => formats + serializer }
  }
}
