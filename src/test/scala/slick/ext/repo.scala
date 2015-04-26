package slick.ext

import slick.ext.macros._

trait Repo {

  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  val DB: Database

  @table[SmallTable](tableName = "foo_table_name")
  class SmallTables {
    def id = column[Option[Long]]("small_table_id")
  }

  @table[LargeTable]
  class LargeTables

  def insertSmallUser(user: SmallTable) = DB.withSession { implicit session =>
    SmallTables.insert(user)
  }

  def insertLargeUser(user: LargeTable) = DB.withSession { implicit session =>
    LargeTables.insert(user)
  }
}
