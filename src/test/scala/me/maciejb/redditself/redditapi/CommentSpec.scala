package me.maciejb.redditself.redditapi

import java.time.LocalDateTime

import com.softwaremill.thegarden.lawn.io.Resources
import me.maciejb.redditself.domain.{Listing, Comment}
import me.maciejb.redditself.redditapi.RedditResponseExampleJsons.WayFairerComments
import org.json4s.jackson.JsonMethods._
import org.scalatest.{FlatSpec, Matchers}

class CommentSpec extends FlatSpec with Matchers {

  import me.maciejb.redditself.redditapi.json4sFormats

  val CommentJObj = (parse(WayFairerComments) \ "data" \ "children" \ "data").children.head

  "json4s formats" should "extract a comment JObject to a valid Comment class" in {
    val comment = CommentJObj.extract[Comment]

    import comment._
    archived shouldEqual false
    body should startWith("Hitler's")
    createdAt shouldEqual LocalDateTime.of(2015, 3, 12, 4, 23, 29)
    linkUrl shouldEqual "http://www.reddit.com/r/AskReddit/comments/2yr4h5/if_facebook_was_around_since_the_dawn_of_man_what/"
    score shouldEqual 31
  }

}

class ListingSpec extends FlatSpec with Matchers {

  val ListingJObj = parse(WayFairerComments)

  "json4s formats" should "extract a listing of comments JObject to a valid Listing[Comment] class" in {
    val listing = Listing.extractComments(ListingJObj)

    listing.after shouldEqual Some("t1_cosj9wo")
    listing.before shouldEqual None

    println(listing.children.mkString("\n"))
  }

}

object RedditResponseExampleJsons {
  val WayFairerComments = Resources.readToString("way_fairer_comments.json")
}