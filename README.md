# slickext
[![Codacy Badge](https://www.codacy.com/project/badge/838cc1f9b9ae4397a3961f9fb1a790fa)](https://www.codacy.com/app/jilen-zhang/slickext)
[![Build Status](https://travis-ci.org/jilen/slickext.svg?branch=master)](https://travis-ci.org/jilen/slickext)

Slick extensions, currently only auto-mapping macros are implemented

## Choose slick Version
[Slick 2.x.x](https://github.com/jilen/slickext/tree/v0.0.2)
[Slick 3.x.x](https://github.com/jilen/slickext)



## How to get this tiny library
Just add bintray repo
```
resolvers += "Jilen Bintray Repo" at "http://dl.bintray.com/jilen/maven"
```
Then includes the dependency and compiler plugin
```
libraryDependencies += "slickext" %% "slickext" % "0.0.4"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```
## Usage

For a case classes (even with more than 22 fields)
```scala
case class SmallTable(
  id: Option[Long],
  a1: Int,
  a2: Int,
  a3: Int,
  a4: Int)
```
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

import slickext.macros._

trait Repo {

  val profile: slick.driver.JdbcProfile
  import profile.api._
  val DB: Database

  @table[SmallTable](tableName = "foo_table_name")
  class SmallTables {
    def id = column[Option[Long]]("small_table_id")
  }

  @table[LargeTable]
  class LargeTables

  def insertSmallUser(user: SmallTable) = DB.run(SmallTables += user)

  def insertLargeUser(user: LargeTable) = DB.run(LargeTables += user)

  def update(user: SmallTable) = {
    val q = SmallTables.filter(_.id === user.id).update(user)
    DB.run(q)
  }
}
```

The `macro` will auto transform the `case class` fields in a `snake case` manner

## Mapping details
+ Table name
  * `LargeTable` is mapped to `large_table`
  * Table name could be override by `@table(tableName = "some_table")`
+ Field map
  * `id: Option[Long]` or `id: Option[Int]` is mapped to `column[Option[X]]("id", O.PrmaryKey, O.AutoInc)`
  * Normal field like `firstName` is mapped to `first_name`
  * Column define could be override manually see the test



## Requirements
+ sbt 0.13.x
+ scala 2.11.x (I believe it works at least with 2.11.4, 2.11.5, 2.11.6, 2.11.7)
+ slick 3.x.x
+ macro paradise compiler plugin




## Contributors
+ [jilen](https://github.com/jilen)
+ [djx314](https://github.com/djx314)
