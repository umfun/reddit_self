package me.maciejb.redditself.personality_insights


trait PersonalityInsightsModule {
  lazy val inputValidator = new InputValidator(InputWordsThresholds.MinimumWordCount)
}