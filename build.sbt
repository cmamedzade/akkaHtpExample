ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.0"

lazy val root = (project in file("."))
  .settings(
    name := "TestService"
  )


val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "io.github.zamblauskas" %% "scala-csv-parser" % "0.13.1",
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "Test",
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % "Test"
)