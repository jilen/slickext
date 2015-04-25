package slick.ext

import org.scalatest._
import org.h2.jdbcx.JdbcDataSource

class TableSpec extends FlatSpec with Matchers with Repo with BeforeAndAfterAll {


  val profile = scala.slick.driver.H2Driver
  import profile.simple._

  override def beforeAll() = DB.withSession { implicit session =>
    (SmallTables.ddl ++ LargeTables.ddl).create
  }

  override val DB = {
    val datasource = new JdbcDataSource()
    datasource.setUrl("jdbc:h2:mem:slickextTest;DB_CLOSE_DELAY=-1")
    Database.forDataSource(datasource)
  }

  "User repo" should "insert small user" in {
    val smallUser = SmallTable(None, 1, 2, 3, 4, 5)
    insertSmallUser(smallUser) should be(1)
  }
}
