
name := "extreme startup"

version := "1.0"

organization := "com.prototypes"

scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.bintrayRepo("hseeberger", "maven"),
  "Artima Maven Repository" at "http://repo.artima.com/releases",
  "Sonatype Repository" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= {

  val circeVersion = "0.7.0"

  Seq(
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-http" % "10.0.6",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
    "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0-M10",

    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6"
  )
}
