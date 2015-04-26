package slick.ext

import org.scalatest._
import org.h2.jdbcx.JdbcDataSource

class RepoSpec extends FlatSpec with Matchers with LoneElement with Repo with BeforeAndAfterAll {

  val profile = scala.slick.driver.H2Driver
  import profile.simple._

  val DB = {
    val datasource = new JdbcDataSource()
    datasource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    Database.forDataSource(datasource)
  }

  val smallTableDDL = SmallTables.ddl.createStatements.toSet
  val largeTableDDL = LargeTables.ddl.createStatements.toSet

  override def beforeAll() = DB.withSession { implicit session =>
    (SmallTables.ddl ++ LargeTables.ddl).createStatements.foreach(println)
    (SmallTables.ddl ++ LargeTables.ddl).create
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

  it should "use custom table name" in {
    smallTableDDL.loneElement should include("foo_table_name")
  }

  it should "use name from type" in {
    largeTableDDL.loneElement should include("large_table")
  }

  it should "use custom column" in {
    smallTableDDL.loneElement should include regex "\\bsmall_table_id\\b.*BIGINT"
  }

  it should "use auto generated id column" in {
    largeTableDDL.loneElement should include regex "\\bid\\b.*BIGINT.*GENERATED.*PRIMARY"
  }
}
