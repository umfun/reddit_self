organization := "me.maciejb.reddit"
name := "reddit_self"
version := "1.0"
scalaVersion := "2.11.6"

// Resolvers
resolvers ++= Seq(
  "SoftwareMill Public Releases" at "https://nexus.softwaremill.com/content/repositories/releases/",
  "SoftwareMill Public Snapshots" at "https://nexus.softwaremill.com/content/repositories/snapshots/"
)

// Dependencies
val gardenVersion = "0.0.31-SNAPSHOT"

val theGarden = Seq("lawn") map {"com.softwaremill.thegarden" %% _ % gardenVersion}

val dispatch = "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"

val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
val testDependencies = Seq(scalaTest)

val logback = "ch.qos.logback" % "logback-classic" % "1.1.1"
val slf4j = "org.slf4j" % "slf4j-api" % "1.7.10"
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
val logging = Seq(logback, slf4j, scalaLogging)

val json4sVersion = "3.2.11"
val json4s = Seq(
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.json4s" %% "json4s-ext" % json4sVersion,
  "com.softwaremill.thegarden" %% "garden-json4s" % gardenVersion
)

/* not used */
val jodaTime = Seq(
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7"
)

libraryDependencies ++= Seq(dispatch) ++ testDependencies ++ logging ++ json4s ++ theGarden

cancelable in Global := true

/* Reddit tests */
lazy val RedditTests = config("reddit") extend Test

lazy val root = Project(id = "reddit_self", base = file(".")).
  configs(RedditTests).
  settings(inConfig(RedditTests)(Defaults.testTasks): _*)

/* Exclude Reddit tests when running 'sbt test' */
testOptions in Test := Seq(Tests.Argument("-l", "RequiresReddit"))
testOptions in RedditTests := Seq(Tests.Argument())