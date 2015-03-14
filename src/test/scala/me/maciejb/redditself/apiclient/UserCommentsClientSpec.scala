
package me.maciejb.redditself.apiclient

import me.maciejb.redditself.Username
import me.maciejb.redditself.common.RequiresReddit
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

/*
 * Setup of tests tagged with RequiresReddit:
 * http://code.hootsuite.com/tagged-tests-with-sbt/
 */

class UserCommentsClientSpec extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  val WayFairer = Username("way_fairer")
  val client = new UserCommentsClient(WayFairer)

  it should "fetch user comments from Reddit" taggedAs RequiresReddit in {
    whenReady(client.commentsJsonStr) { json =>
      json should include("http://www.reddit.com/")
    }
  }

  it should "parse user comments from Reddit" taggedAs RequiresReddit in {
    whenReady(client.comments) { comments =>
      comments.length should be > 0
    }
  }

}
