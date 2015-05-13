package me.maciejb.redditself.dataaccess.reddit

import me.maciejb.redditself.Username
import me.maciejb.redditself.redditapi.{UserCommentsClient, Comment}

import scala.concurrent.Future

class ByUserFetcher(client: UserCommentsClient, wordsCountThreshold: Int) {

  def getComments(user: Username) : Future[String] = ???

}

case class TooFewComments(comments: List[Comment], length: Int) extends Exception
