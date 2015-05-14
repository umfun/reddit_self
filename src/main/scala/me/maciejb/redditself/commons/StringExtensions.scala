package me.maciejb.redditself.commons


object StringExtensions {

  implicit class RichString(str: String) {
    def countWords = str.split( """\W+""").length
  }

}