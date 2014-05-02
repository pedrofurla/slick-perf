package support

import slickperf.old.TablesOld

/**
 * Created by pedrofurla on 01/05/14.
 */
object SlickInstances {
  val mySqlConnection = JdbcConnectionTemplate (
     url = "jdbc:mysql://localhost:3306/slickperf",
     username = "root",
     password = "",
     driver = "com.mysql.jdbc.Driver"
  )

  val postgresConnection = JdbcConnectionTemplate (
     url = "jdbc:postgresql://127.0.0.1:5432/slickperf",
     username = "root",
     password = "",
     driver = "org.postgresql.Driver"
  )

  val mySqlConnection2 = JdbcConnectionTemplate (
     url = "jdbc:mysql://localhost:3306/jpa2",
     username = "root",
     password = "",
     driver = "com.mysql.jdbc.Driver"
  )

  object SlickMySqlOld extends {
    val jdbc = mySqlConnection
    val profile = scala.slick.driver.MySQLDriver
  } with StdSlickSupport with TablesOld

  object SlickPostgresOld extends {
    val jdbc = postgresConnection
    val profile = scala.slick.driver.PostgresDriver
  } with StdSlickSupport with TablesOld

  object SlickMySql extends {
    val jdbc = mySqlConnection2
    val profile = scala.slick.driver.MySQLDriver
  } with SlickSupport with slickperf.Tables
}
