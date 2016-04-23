import SonatypeKeys._

sonatypeSettings

name := "anorm-relational"

version := "0.2.0"

crossScalaVersions := Seq("2.10.6", "2.11.8")

organization := "com.jaroop"

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "2.3.12" % "test",
    "com.typesafe.play" %% "anorm" % "2.5.0"
)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

pomExtra := {
  <url>https://github.com/mhzajac/anorm-relational</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:github.com/mhzajac/anorm-relational</connection>
    <developerConnection>scm:git:git@github.com:mhzajac/anorm-relational</developerConnection>
    <url>github.com/mhzajac/anorm-relational</url>
  </scm>
  <developers>
    <developer>
      <id>mz</id>
      <name>Michael Zajac</name>
      <url>https://github.com/mhzajac</url>
    </developer>
  </developers>
}
