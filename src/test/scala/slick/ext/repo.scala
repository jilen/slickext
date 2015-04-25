package slick.ext

import slick.ext.macros._

trait UserRepo {
  //val profile: scala.slick.driver.JdbcProfile
  val profile = scala.slick.driver.H2Driver
  import profile.simple._
  import scala.slick.collection.heterogenous._
  
  val db = Database.forURL("jdbc:h2:mem:slickextTest", driver = "org.h2.Driver")

  @table[User](tableName = "user")
  class Users
  
  //I suggest that TableQuery's name use model + "Table" to avoid the table name like "news"
  @table[TestUser](tableName = "test_user")
  class TestUserTable
  
}

object TestCase extends App {
  
  object UserRepo extends UserRepo
  
  
  import UserRepo._
  import UserRepo.profile.simple._
  db withSession { implicit session =>
    Users.ddl.create
    TestUserTable.ddl.create
    TestUserTable ++= TestUser(None, Option(2333), "东方") :: TestUser(None, Option(6666), "在地下城寻求偶遇是否搞错了什么") :: TestUser(None, Option(9527), "刀剑神域") :: Nil
    println(TestUserTable.list.mkString("\n\n"))
  }
  
}