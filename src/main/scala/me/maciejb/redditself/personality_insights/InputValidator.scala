package me.maciejb.redditself.personality_insights


class InputValidator(minimumWordCount: Int) {

  def validate(input: String): List[ValidationFailure[String]] =
    (validateSingle(wordCount(input) > minimumWordCount,
      ValidationFailure(input, "Too few words")) :: Nil).flatten

  private def validateSingle[T](validation: => Boolean, failure: => ValidationFailure[T]) =
    if (!validation) Some(failure)
    else None

  private[personality_insights] def wordCount(input: String) = input.split( """\W+""").length

}

case class ValidationFailure[T](input: T, message: String)
