package slickext

import org.h2.jdbcx.JdbcDataSource
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import scala.concurrent.duration._
import scala.concurrent.Await

class RepoSpec extends FlatSpec
    with Matchers
    with ScalaFutures
    with LoneElement
    with Repo
    with BeforeAndAfterAll {

  val profile = slick.driver.H2Driver
  import profile.api._

  val DB = {
    val datasource = new JdbcDataSource()
    datasource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    Database.forDataSource(datasource)
  }

  val smallTableDDL = SmallTables.schema.createStatements.toSet
  val largeTableDDL = LargeTables.schema.createStatements.toSet

  override def beforeAll() = Await.result(
    DB.run((SmallTables.schema ++ LargeTables.schema).create),
    1.seconds)


  "User repo" should "insert into small table" in {
    val small = SmallTable(None, 1, 2, 3, 4)
    insertSmallUser(small).futureValue  should be(1)
    update(small)
  }

  it should "insert into large table" in {
    val large =LargeTable(
      None,
      1, 2, 3, 4, 5,
      6, 7, 8, 9, 10,
      11, 12, 13, 14,15,
      16, 17, 18, 19, 20,
      21, 22, 23)

    insertLargeUser(large).futureValue should be(1)
  }

  it should "use custom table name" in {
    smallTableDDL.loneElement should include("\"foo_table_name\"")
  }

  it should "use name from type" in {
    largeTableDDL.loneElement should include("\"large_table\"")
  }

  it should "use custom column" in {
    smallTableDDL.loneElement should include regex "\\bsmall_table_id\\b.*BIGINT"
  }

  it should "use auto generated id column" in {
    largeTableDDL.loneElement should include regex "\\bid\\b.*BIGINT.*GENERATED.*PRIMARY"
  }
}
