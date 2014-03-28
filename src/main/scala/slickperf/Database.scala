package slickperf

import scala.slick.driver.JdbcProfile

trait ConnectionTemplate {
  val env:String
  val url:String
  val username:String
  val password:String
  val driver:String

}

trait MySqlConection extends ConnectionTemplate {
  val env = scala.util.Properties.envOrElse("runMode", "prod")
  val url = "jdbc:mysql://localhost:3306/slickperf"
  val username = "root"
  val password = "1root"
  val driver = "scala.slick.driver.MySQLDriver"
}

trait SlickProfile {
  val profile: JdbcProfile
  val simple:profile.simple.type = profile.simple
}

trait MySqlProfile extends SlickProfile with MySqlConection {
  val profile = scala.slick.driver.MySQLDriver
}

trait DBConnection {
  this: ConnectionTemplate with SlickProfile =>

  import profile.simple._

  def connect(): Database =  Database.forURL( url, username, password,null, driver)

}

object MySqlConnection extends DBConnection with MySqlProfile