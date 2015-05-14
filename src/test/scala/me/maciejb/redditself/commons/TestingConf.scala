package me.maciejb.redditself.commons

import com.typesafe.config.{ConfigFactory, Config}


trait TestingConf {
  def config: Config

  def signalFxAuthToken = config.getString("metrics.signal_fx.auth_token")
}

object TestingConf extends TestingConf {
  override lazy val config = ConfigFactory.load("testing.conf")
}
