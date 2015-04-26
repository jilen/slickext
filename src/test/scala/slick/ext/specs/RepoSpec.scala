package slick.ext

import org.scalatest._
import org.h2.jdbcx.JdbcDataSource

class RepoSpec extends FlatSpec with Matchers with Repo with BeforeAndAfterAll {

  val profile = scala.slick.driver.H2Driver
  import profile.simple._

  override def beforeAll() = DB.withSession { implicit session =>
    (SmallTables.ddl ++ LargeTables.ddl).create
  }

  val DB = {
    val datasource = new JdbcDataSource()
    datasource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    Database.forDataSource(datasource)
  }

  "User repo" should "insert into small table" in {
    val small = SmallTable(None, 1, 2, 3, 4)
    insertSmallUser(small) should be(1)
  }

  it should "insert into large table" in {
    val large =LargeTable(
      None,
      1, 2, 3, 4, 5,
      6, 7, 8, 9, 10,
      11, 12, 13, 14,15,
      16, 17, 18, 19, 20,
      21, 22, 23)
    insertLargeUser(large) should be(1)
  }
}
