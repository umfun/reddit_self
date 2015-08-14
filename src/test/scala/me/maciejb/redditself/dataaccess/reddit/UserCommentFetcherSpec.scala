package me.maciejb.redditself.dataaccess.reddit

import me.maciejb.redditself.commons.StringExtensions._
import me.maciejb.redditself.commons.{Redditers, RequiresReddit}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{FlatSpec, Matchers}

class UserCommentFetcherSpec extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  def module(inputWordThreshold: Int, tooFewCommentsThreshold: Int) = new DataAccessRedditModule {
    override val userCommentFetcher = new UserCommentFetcher(userCommentsClient,
      inputWordThreshold, tooFewCommentsThreshold)
  }

  it should "fetch at comments with at least 2.000 words if requested" taggedAs RequiresReddit in {
    val sizeOfDatasetToRequest = 2000
    val fetcher = module(sizeOfDatasetToRequest, 0).userCommentFetcher
    whenReady(fetcher.getComments(Redditers.WayFairer)) { comments =>
      try {
        comments.countWords shouldBe >(sizeOfDatasetToRequest)
      } catch {
        case failure: TestFailedException =>
          Console.err.println(s"Comments string too short: $comments.")
          throw failure
      }
    }
  }

}
