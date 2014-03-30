package slickperf

import client.test.TestHelper._

import MySqlConnection._
import profile._
import MySqlConnection.simple._

object SlickInsert {

  def main(args: Array[String]) {
    val newUsers = MainTcUser.map(s => s) returning MainTcUser.map(_.id)
    newUsers.insertStatement

    val accounts = PayTdAccount.map(s => s) returning PayTdAccount.map(_.id) // PayTdAccount.insertInvoker
    accounts.insertStatement
    
    for (i <- numberOfInserts) {
      add(newUsers, accounts, i)
    }
  }

  private def add(newUsers: KeysInsertInvoker[MainTcUserRow, Int],
                  accounts: KeysInsertInvoker[PayTdAccountRow, Int], number: Int) {
    val duration = chronometer {
      connect.withTransaction { implicit session =>
        for (i <- 1 to number) {
          val user = MainTcUserRow(0, Some("test"), Some("test"), Some("test"), Some("test"))
          val id = (newUsers) += user
          val account = PayTdAccountRow(0, Some(500), Some(200), id)
          accounts += account
        }
      }
    }
    printTime(duration,number, MS)
  }
}