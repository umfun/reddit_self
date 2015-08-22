package me.maciejb.redditself.model

// If ever in need of unmarshalling this with spray-json consider looking at:
// https://groups.google.com/forum/#!topic/spray-user/8bfuxj1qHKA
case class Fullname(v: String) extends AnyVal
