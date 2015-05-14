package me.maciejb.redditself.personality_insights

import me.maciejb.redditself.commons.HipsterIpsum
import org.scalatest.{WordSpec, Matchers, FlatSpec}


class InputValidatorSpec extends WordSpec with Matchers {

  val inputValidator = new InputValidator(100)

  "wordcount" when {
    "given a string with a double whitespace" should {
      "ignore it" in {
        inputValidator.wordCount("foo  bar") shouldEqual 2
      }
    }

    "given a string with a tabulator" should {
      "ignore it" in {
        inputValidator.wordCount("foo\tbar") shouldEqual 2
      }
    }
  }

  "The validator" when {
    "given a sufficiency long input string" should {
      "return a Nil list of ValidationFailures" in {
        inputValidator.validate(HipsterIpsum.Paragraphs4) shouldEqual Nil
      }
    }

    "given a too short string" should {
      "return a list containing a /too short/ validation failure" in {
        val validationFailures = inputValidator.validate("foo bar")

        validationFailures.size shouldEqual 1
        validationFailures.head.message.toLowerCase should include("too few")
      }
    }
  }


}
