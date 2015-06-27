package slickext

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
}
