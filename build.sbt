name := "anormext"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalaBinaryVersion := "2.10"

organization := "com.jaroop"

libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "2.3.12" % "test",
 	"com.typesafe.play"  %%   "anorm" % "2.3.0"
)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"