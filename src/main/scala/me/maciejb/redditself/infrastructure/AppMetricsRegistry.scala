package me.maciejb.redditself.infrastructure

import com.codahale.metrics.MetricRegistry
import nl.grons.metrics.scala.InstrumentedBuilder

object AppMetricsRegistry {
  val metricRegistry = new MetricRegistry
}

trait Instrumented extends InstrumentedBuilder {
  override val metricRegistry = AppMetricsRegistry.metricRegistry
}
