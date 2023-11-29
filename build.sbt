name         := "slickext"
scalaVersion := "2.11.12"
organization := "slickext"

def slickV(sv: String): String = {
  if (sv.startsWith("2.11")) "3.3.3" else "3.4.1"
}

libraryDependencies ++= Seq(
  "org.scala-lang"      % "scala-compiler" % scalaVersion.value,
  "com.typesafe.slick" %% "slick"          % slickV(scalaVersion.value),
  "org.scalatest"      %% "scalatest"      % "3.0.8"   % Test,
  "com.h2database"      % "h2"             % "1.4.187" % Test
)

val crossVersionSettings = Seq(
  crossScalaVersions := Seq("2.11.12", "2.12.18", "2.13.12"),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n <= 12 =>
        List(
          compilerPlugin(
            "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
          )
        )
      case _ => Nil
    }
  },
  Compile / scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n <= 12 => Nil
      case _                       => List("-Ymacro-annotations")
    }
  },
  scalacOptions ++= Seq("-deprecation", "-feature")
)

lazy val root = project.in(file(".")).settings(crossVersionSettings: _*)
