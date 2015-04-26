# slickext
[![Codacy Badge](https://www.codacy.com/project/badge/838cc1f9b9ae4397a3961f9fb1a790fa)](https://www.codacy.com/app/jilen-zhang/slickext)
[![Build Status](https://travis-ci.org/jilen/slickext.svg?branch=master)](https://travis-ci.org/jilen/slickext)

Slick extensions, currently only auto-mapping macros are implemented

## Auto mapping for case class

For a `case class`
```scala
case class User(id: Option[Long], firstName: String, lastName: String, gender: String)
```
We must write an `Service` or `Repo` like this
```scala
trait UserRepo { profile: slick.jdbc.JdbcProfile =>

  import profile.simple._

  class Users(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Option[Long]]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("first_name")
    def birth = column[String]("last_name")
    def gender = column[String]("gender")
    def * = (id, name, birth, gender) <> (User.tupled, User.unapply)
  }

  val Users = TableQuery[Users]
  ...
}
```

Now with the macro annotation `slick.ext.table`, we just need
```scala
trait UserRepo { profile: slick.jdbc.JdbcProfile =>

  import profile.simple._

  @table[User](tanleName = "user")
  class Users
  ...
}

```

#### Requirements
+ sbt 0.13.x
+ scala 2.11.6 (may work with other scala 2.11.x, but you may need build it yourself)
+ slick 2.1.0 (may or may not work with other version)

## Contributors
+ [jilen](https://github.com/jilen)
+ [djx314](https://github.com/djx314)
