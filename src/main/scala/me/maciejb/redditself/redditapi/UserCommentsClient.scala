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
  extends Instrumented with FutureMetrics with ListingRequest[Comment] {

  val commentsUri = {
    val queryParams = QueryParam.toDispatch(QueryParam.fromFullname("before", before)
      :: QueryParam.fromFullname("after", after) :: Nil)
    url(s"https://www.reddit.com/user/${user.value}/comments.json") <<? queryParams
  }

  def listing(): Future[Listing[Comment]] = {
    for (str <- dispatch()) yield {Listing.extractComments(str)}
  }

  private def dispatch(): Future[String] =
    timing(UserCommentsClient.ReqTimer) {Http(commentsUri OK as.String)}

}

trait ListingRequest[T] {
  def listing(): Future[Listing[T]]
}

object FutureExtensions {
  implicit class FutureExt[T](future: Future[T]) {

    def foldUntil[A](acc: A)(untilCond: A => Boolean,
                             nextFutureFactory: (A) => Future[T],
                             mergeFun: (A, T) => A): Future[T] = {
      future.flatMap { v =>
        val newAcc = mergeFun(acc, v)
        if (untilCond(newAcc)) {
          nextFutureFactory(newAcc)
        } else {
          foldUntil(newAcc)(untilCond, nextFutureFactory, mergeFun)
        }
      }
    }

    def reduceWithNext(nextFutureFactory: (T) => Option[Future[T]]): Future[T] = {
      future.flatMap { v =>
        nextFutureFactory(v).getOrElse(Future.successful(v))
      }
    }
  }

}

class ListingForwardScanner[T](reqFactory: Option[Listing[T]] => Option[ListingRequest[T]],
                               listingOpt: Option[Listing[T]],
                               acc: List[T]) {

  //  private def progress(): Future[Either[ListingForwardScanner[T], List[T]]] = {
  //    val next = reqFactory(listingOpt) match {
  //      case Some(req) =>
  //        for {
  //          listing <- req.listing()
  //        } yield {
  //          Left(new ListingForwardScanner[T](reqFactory, listingOpt, listing.children ::: acc))
  //        }
  //      case None => Future.successful {
  //        Right(acc)
  //      }
  //    }
  //
  //  }
  //
  //  def resolve(): Future[List[T]] = for (fVal <- progress()) yield {
  //    fVal match {
  //      case _: Left => sys.error("Whoa! That's a endless loop!")
  //      case Right(finalAcc) => finalAcc
  //    }
  //  }

}


class UserCommentsClient {

  private def next(user: Username, futureListing: Future[Listing[Comment]], acc: List[Comment],
                   untilPredicate: List[Comment] => Boolean = (_) => true): Future[List[Comment]] = {
    futureListing.flatMap { listing =>
      val newAcc = acc ::: listing.children
      (untilPredicate(newAcc), listing.after) match {
        case (false, _) | (true, None) => Future {newAcc}
        case (true, Some(_)) => next(user,
          CommentsRequest(user, after = listing.after map Fullname).listing(), newAcc, untilPredicate)
      }
    }
  }

  /*
   * Public API
   */

  def latestComments(user: Username): Future[List[Comment]] = for (listing <- CommentsRequest(user).listing())
    yield {listing.children}

  def allComments(user: Username): Future[List[Comment]] = next(user, CommentsRequest(user).listing(), Nil)

  def commentsUntil(user: Username, untilPredicate: List[Comment] => Boolean): Future[List[Comment]] =
    next(user, CommentsRequest(user).listing(), Nil, untilPredicate)

}

object UserCommentsClient {
  val ReqTimer = "req"
}
