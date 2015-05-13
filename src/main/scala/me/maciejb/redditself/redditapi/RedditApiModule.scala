package me.maciejb.redditself.redditapi

import com.softwaremill.macwire.Macwire

trait RedditApiModule extends Macwire {

  val userCommentsClient = wire[UserCommentsClient]

}
