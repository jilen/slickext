package slick.ext

import slick.ext.macros._
import scala.slick.collection.heterogenous._

trait Repo {

  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  val DB: Database
  
  @table[SmallTable](tableName = "smalluser")
  class SmallTables

  @table[LargeTable](tableName = "largeuser")
  class LargeTables

  def insertSmallUser(user: SmallTable) = DB.withSession { implicit session =>
    SmallTables.insert(user)
  }

  def insertLargeUser(user: LargeTable) = DB.withSession { implicit session =>
    LargeTables.insert(user)
  }
  
}