# slickext
Slick extensions, currently only auto-mapping macros are implemented

## Auto mapping for small case class (with less than 22 fields)

### Getting start

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
