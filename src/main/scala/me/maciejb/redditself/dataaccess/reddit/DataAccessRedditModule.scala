package me.maciejb.redditself.dataaccess.reddit

import com.softwaremill.macwire.Macwire
import me.maciejb.redditself.infrastructure.InfrastructureModule
import me.maciejb.redditself.redditapi.RedditApiModule

trait DataAccessRedditModule extends Macwire with RedditApiModule with InfrastructureModule {
  val userCommentFetcher = new UserCommentFetcher(userCommentsClient)
}
