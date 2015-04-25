name := "slick-ext"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"


libraryDependencies ++= Seq(
  "org.scala-lang"     % "scala-compiler" % scalaVersion.value,
  "com.typesafe.slick" %% "slick"         % "2.1.0",
  "org.scalatest"      %% "scalatest"     % "2.2.1"              % "test",
  "com.h2database"     %   "h2"           % "1.4.187"            % "test"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

scalacOptions ++= Seq("-deprecation")
