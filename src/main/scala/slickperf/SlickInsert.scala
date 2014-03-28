package slickperf

import slickperf.Tables._
import client.test.TestHelper._

import MySqlConnection._
import simple._

object MainInsert {

  def main(args: Array[String]) {
    val newUsers = Tables.MainTcUser.map(s => s) returning Tables.MainTcUser.map(_.id)
    newUsers.insertStatement

    val accounts = Tables.PayTdAccount.insertInvoker
    accounts.insertStatement
    
    for (i <- numberOfInserts) {
      add(newUsers, accounts, i)
    }
  }

  private def add(newUsers: scala.slick.driver.MySQLDriver.KeysInsertInvoker[Tables.MainTcUserRow, Int], accounts: scala.slick.driver.MySQLDriver.CountingInsertInvoker[PayTdAccount#TableElementType], number: Int) {
    val duration = chronometer {
      connect.withTransaction { implicit session =>
        for (i <- 1 to number) {
          val user = Tables.MainTcUserRow(0, Some("test"), Some("test"), Some("test"), Some("test"))
          val id = (newUsers) += user
          val account = Tables.PayTdAccountRow(0, Some(500), Some(200), id)
          accounts += account
        }
      }
    }
    printTime(duration, SEC)
  }
}