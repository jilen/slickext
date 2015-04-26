package slick.ext

import slick.ext.macros._

trait Repo {

  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  val DB: Database

  @table[SmallTable]
  class SmallTables

  @table[LargeTable]
  class LargeTables

  def insertSmallUser(user: SmallTable) = DB.withSession { implicit session =>
    SmallTables.insert(user)
  }

  def insertLargeUser(user: LargeTable) = DB.withSession { implicit session =>
    LargeTables.insert(user)
  }
}
