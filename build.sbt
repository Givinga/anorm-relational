name := "anormext"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalaBinaryVersion := "2.10"

organization := "com.jaroop"

libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "2.3.12" % "test"
)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

