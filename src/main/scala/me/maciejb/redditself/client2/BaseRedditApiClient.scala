package me.maciejb.redditself.client2

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization


trait BaseRedditApiClient extends Json4sSupport {
  implicit val serialization = Serialization
  implicit val json4sFormat = DefaultFormats
}
