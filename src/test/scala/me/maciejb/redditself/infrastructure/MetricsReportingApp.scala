package me.maciejb.redditself.infrastructure

import java.util.concurrent.TimeUnit

import com.codahale.metrics.Slf4jReporter
import com.signalfx.codahale.reporter.SignalFxReporter.Builder
import dispatch.Defaults._
import dispatch._
import me.maciejb.redditself.commons.{Redditers, TestingConf}
import me.maciejb.redditself.infrastructure.AppMetricsRegistry.metricRegistry
import me.maciejb.redditself.redditapi.{UserCommentsClient, UserCommentsClientSpec}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

/**
 * Manual test that should report a metric.
 */

object MetricsReportingApp extends App {

  val commentsClient = new UserCommentsClient

  def getComments = Future.sequence((0 to 10).
    map(_ => commentsClient.latestComments(Redditers.WayFairer)))

  def setupMetrics = {
    val logReporter = Slf4jReporter.forRegistry(metricRegistry)
      .outputTo(LoggerFactory.getLogger("me.maciejb.redditself.metrics"))
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()

    val signalFxReporter = new Builder(metricRegistry, TestingConf.signalFxAuthToken).
      setDurationUnit(TimeUnit.SECONDS).
      setRateUnit(TimeUnit.MILLISECONDS).
      setName("reddit_self").
      build()

    Seq(logReporter, signalFxReporter)
  }

  val metricReporters = setupMetrics
  val futComments = getComments

  futComments onComplete { _ => metricReporters.foreach { reporter =>
    reporter.report()
    reporter.stop()
  }
  }

  futComments onComplete { _ => Http.shutdown() }
}
