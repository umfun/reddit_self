organization := "me.maciejb.reddit"
name := "reddit_self"
version := "1.0"
scalaVersion := "2.11.7"

// Resolvers
resolvers ++= Seq(
  "Garden repository" at "https://dl.bintray.com/maciej/maven/",
  "hseeberger at bintray" at "http://dl.bintray.com/hseeberger/maven"
)

// Dependencies
val gardenVersion = "0.0.38"

val garden = Seq("garden-lawn") map {"me.maciejb.garden" %% _ % gardenVersion}
val macwire = Seq("macros", "runtime") map ("com.softwaremill.macwire" %% _ % "1.0.5")

val dispatch = "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"

val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
val testDependencies = Seq(scalaTest)

val logback = "ch.qos.logback" % "logback-classic" % "1.1.1"
val slf4j = "org.slf4j" % "slf4j-api" % "1.7.12"
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
val logging = Seq(logback, slf4j, scalaLogging)

val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

val json4sVersion = "3.3.0.RC3"
val json4s = Seq(
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.json4s" %% "json4s-ext" % json4sVersion,
  "me.maciejb.garden" %% "garden-json4s" % gardenVersion
)

val metricsCore = "io.dropwizard.metrics" % "metrics-core" % "3.1.0"
val metricsScala = "nl.grons" %% "metrics-scala" % "3.3.0_a2.3"
val signalFx = "com.signalfx.public" % "signalfx-codahale" % "0.0.20"
val metrics = Seq(metricsCore, metricsScala, signalFx)

val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.2"

val akkaVersion = "2.3.12"
val akkaCore = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)

val akkaStreams = "com.typesafe.akka" %% "akka-stream-experimental" % "1.0"
val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0"
// https://github.com/hseeberger/akka-http-json
val akkaHttpJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.0.0"

val akka = akkaCore ++ Seq(akkaStreams, akkaHttpCore, akkaHttpJson4s)

libraryDependencies ++= Seq(dispatch, typesafeConfig, scalaAsync) ++ testDependencies ++ logging ++ json4s ++
  garden ++ macwire ++ metrics ++ akka

cancelable in Global := true

/* Reddit tests */
lazy val RedditTests = config("reddit") extend Test

lazy val root = Project(id = "reddit_self", base = file(".")).
  configs(RedditTests).
  settings(inConfig(RedditTests)(Defaults.testTasks): _*)

/* Exclude Reddit tests when running 'sbt test' */
testOptions in Test := Seq(Tests.Argument("-l", "RequiresReddit"))
testOptions in RedditTests := Seq(Tests.Argument())
