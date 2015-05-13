package me.maciejb.redditself.dataaccess.reddit

import com.softwaremill.macwire.Macwire
import me.maciejb.redditself.personality_insights.InputWordsThresholds
import me.maciejb.redditself.redditapi.RedditApiModule

trait DataAccessRedditModule extends Macwire with RedditApiModule {
  val byUserFetcher = new ByUserFetcher(userCommentsClient, InputWordsThresholds.DesiredWordCount)
}
