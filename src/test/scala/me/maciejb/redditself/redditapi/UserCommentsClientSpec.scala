
package me.maciejb.redditself.redditapi

import me.maciejb.redditself.commons.{Redditers, RequiresReddit}
import me.maciejb.redditself.redditapi.dtos.Username
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

/*
 * Setup of tests tagged with RequiresReddit:
 * http://code.hootsuite.com/tagged-tests-with-sbt/
 */

class UserCommentsClientSpec extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  val client = new UserCommentsClient

  it should "parse user comments from Reddit" taggedAs RequiresReddit in {
    whenReady(client.latestComments(Redditers.WayFairer)) { comments =>
      comments.length should be > 0
    }
  }

}
