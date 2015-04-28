# slickext
[![Codacy Badge](https://www.codacy.com/project/badge/838cc1f9b9ae4397a3961f9fb1a790fa)](https://www.codacy.com/app/jilen-zhang/slickext)
[![Build Status](https://travis-ci.org/jilen/slickext.svg?branch=master)](https://travis-ci.org/jilen/slickext)

Slick extensions, currently only auto-mapping macros are implemented

## How to get this tiny library
Just add bintray repo
```
resolvers += "Jilen Bintray Repo" at "http://dl.bintray.com/jilen/maven"
```
Then includes the dependency and compiler plugin
```
libraryDependencies += "slick-ext" %% "slick-ext" % "0.0.1"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```
## Usage

For a case class
```scala
case class LargeTable(
  id: Option[Long],
  a1: Int,
  a2: Int,
  a3: Int,
  a4: Int,
  a5: Int,
  a6: Int,
  a7: Int,
  a8: Int,
  a9: Int,
  a10: Int,
  a11: Int,
  a12: Int,
  a13: Int,
  a14: Int,
  a15: Int,
  a16: Int,
  a17: Int,
  a18: Int,
  a19: Int,
  a20: Int,
  a21: Int,
  a22: Int,
  a23: Int)
```

We can just write a `repository` or `service` component as
```scala
trait Repo {

  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  val DB: Database

  @table[LargeTable]
  class LargeTables

  def insertLargeUser(user: LargeTable) = DB.withSession { implicit session =>
    LargeTables.insert(user)
  }

```

The `macro` will auto transform the `case class` fields in a `snake case` manner


## Requirements
+ sbt 0.13.x
+ scala 2.11.6 (I believe it works at least with 2.11.4, 2.11.5, 2.11.6)
+ slick 2.1.0 (May or may not work with other version)
+ macro paradise compiler plugin

## Contributors
+ [jilen](https://github.com/jilen)
+ [djx314](https://github.com/djx314)
