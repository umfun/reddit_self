package me.maciejb.redditself.redditapi

import java.time.LocalDateTime

import com.softwaremill.thegarden.lawn.io.Resources
import org.scalatest.{Matchers, FlatSpec}
import org.json4s.jackson.JsonMethods._

class CommentSpec extends FlatSpec with Matchers {

  import me.maciejb.redditself.redditapi.json4sFormats

  val JsonStr = Resources.readToString("way_fairer_comments.json")

  val ExampleCommentObject = (parse(JsonStr) \ "data" \ "children" \ "data").children(0)

  "json4s formats" should "extract a comment JObject to a valid Comment class" in {
    val comment = ExampleCommentObject.extract[Comment]

    import comment._
    archived shouldEqual false
    body should startWith("Hitler's")
    createdAt shouldEqual LocalDateTime.of(2015, 3, 12, 4, 23, 29)
    linkUrl shouldEqual "http://www.reddit.com/r/AskReddit/comments/2yr4h5/if_facebook_was_around_since_the_dawn_of_man_what/"
    score shouldEqual 31
  }

}
