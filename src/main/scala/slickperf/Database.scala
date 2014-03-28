package slickperf

import scala.slick.driver.JdbcProfile

trait ConnectionTemplate {
  val url:String
  val username:String
  val password:String
  val driver:String

}

trait MySqlConection extends ConnectionTemplate {
  val url = "jdbc:mysql://localhost:3306/slickperf"
  val username = "root"
  val password = ""
  val driver = "scala.slick.driver.MySQLDriver"
}

trait SlickProfile {
  val profile: JdbcProfile
  lazy val simple:profile.simple.type = profile.simple
}

trait MySqlProfile extends SlickProfile with MySqlConection {
  val profile = scala.slick.driver.MySQLDriver
}

trait DBConnection {
  this: ConnectionTemplate with SlickProfile with Tables =>

  import simple._
  def connect: Database =  Database.forURL( url, username, password,null, driver)

  import profile.Implicit._
  def createSchema = connect withSession { s => ddl.create(s) }
  def destroySchema = connect withSession { s => ddl.drop(s) }
}

object MySqlConnection extends DBConnection with MySqlProfile with Tables