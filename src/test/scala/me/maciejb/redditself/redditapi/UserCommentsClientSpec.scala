
package me.maciejb.redditself.redditapi

import me.maciejb.redditself.Username
import me.maciejb.redditself.commons.RequiresReddit
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

/*
 * Setup of tests tagged with RequiresReddit:
 * http://code.hootsuite.com/tagged-tests-with-sbt/
 */

class UserCommentsClientSpec extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  import UserCommentsClientSpec._

  val client = new UserCommentsClient

  it should "parse user comments from Reddit" taggedAs RequiresReddit in {
    whenReady(client.latestComments(WayFairer)) { comments =>
      comments.length should be > 0
    }
  }

}

object UserCommentsClientSpec {
  val WayFairer = Username("way_fairer")
}
