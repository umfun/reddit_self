package me.maciejb.redditself.dataaccess.reddit

import me.maciejb.redditself.personality_insights.InputWordsThresholds
import me.maciejb.redditself.redditapi.UserCommentsClient

import me.maciejb.redditself.commons.StringExtensions._
import me.maciejb.redditself.redditapi.domain.{Username, Comment}

import scala.concurrent.{ExecutionContext, Future}

class UserCommentFetcher(client: UserCommentsClient, wordsCountThreshold: Int = InputWordsThresholds.DesiredWordCount,
                         tooFewCommentsThreshold: Int = InputWordsThresholds.MinimumWordCount)
                        (implicit executionContext: ExecutionContext) {

  def getComments(user: Username): Future[String] = {
    client.commentsUntil(user, mergeComments(_).countWords < wordsCountThreshold).
      map(mergeComments).flatMap { collapsedBody =>
      val words = collapsedBody.countWords
      if (words < tooFewCommentsThreshold) Future.failed(TooFewComments(collapsedBody, words))
      else Future.successful(collapsedBody)
    }
  }

  private def mergeComments(list: List[Comment]) = list.map(_.body).mkString("\n")

}

case class TooFewComments(commentsBody: String, length: Int) extends Exception
