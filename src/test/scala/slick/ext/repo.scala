package slick.ext

import slick.ext.macros._

trait UserRepo {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._

  @table[User](tableName = "user")
  class Users
}
