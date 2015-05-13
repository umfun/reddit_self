package me.maciejb.redditself.redditapi

import dispatch.Defaults._
import dispatch._
import me.maciejb.redditself.Username
import me.maciejb.redditself.infrastructure.Instrumented
import me.maciejb.redditself.redditapi.internal.QueryParam
import nl.grons.metrics.scala.FutureMetrics

private[redditapi] case class CommentsRequest(user: Username,
                                              before: Option[Fullname] = None,
                                              after: Option[Fullname] = None)
  extends Instrumented with FutureMetrics {

  val commentsUri = {
    val queryParams = QueryParam.toDispatch(QueryParam.fromFullname("before", before)
      :: QueryParam.fromFullname("after", after) :: Nil)
    url(s"https://www.reddit.com/user/${user.value}/comments.json") <<? queryParams
  }

  def commentsListing(): Future[Listing[Comment]] = {
    for (str <- dispatch()) yield {Listing.extractComments(str)}
  }

  private def dispatch(): Future[String] =
    timing(UserCommentsClient.ReqTimer) {Http(commentsUri OK as.String)}

}

class UserCommentsClient {

  private def next(user: Username, futureListing: Future[Listing[Comment]], acc: List[Comment],
                   untilCond: List[Comment] => Boolean = (_) => true): Future[List[Comment]] = {
    futureListing.flatMap { listing =>
      val newAcc = acc ::: listing.children
      (untilCond(newAcc), listing.after) match {
        case (false, _) | (true, None) => Future {acc}
        case (true, Some(_)) => next(user,
          CommentsRequest(user, after = listing.after).commentsListing(), newAcc, untilCond)
      }
    }
  }

  /*
   * Public API
   */

  def latestComments(user: Username): Future[List[Comment]] = for (listing <- CommentsRequest(user).commentsListing())
    yield {listing.children}

  def allComments(user: Username): Future[List[Comment]] = next(user, CommentsRequest(user).commentsListing(), Nil)

  def commentsUntil(user: Username, untilPredicate: List[Comment] => Boolean): Future[List[Comment]] =
    next(user, CommentsRequest(user).commentsListing(), Nil, untilPredicate)

}

object UserCommentsClient {
  val ReqTimer = "req"
}
