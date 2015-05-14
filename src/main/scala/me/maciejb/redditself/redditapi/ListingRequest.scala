package me.maciejb.redditself.redditapi

import me.maciejb.redditself.redditapi.dtos.Listing

import scala.concurrent.Future

private[redditapi] trait ListingRequest[T] {
  def listing(): Future[Listing[T]]
}