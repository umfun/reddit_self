package me.maciejb.redditself.personality_insights

/**
 * Thresholds for number of words providing better Personality Insights accuracy.
 *
 * @author Maciej Bilas
 * @since 6/4/15 18:30
 * @see http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/doc/personality-insights/overview.shtml#overview
 */
object InputWordsThresholds {
  private val MeaningfulResults = 3500
  private val MediumSamplingError = 6000
  private val LowSamplingError = 25000

  val MinimumWordCount = MeaningfulResults
  val DesiredWordCount = LowSamplingError

}
