package slickperf

import exec.TestHelper._
import exec._
import exec.Reports._
import MySqlConnection._
import MySqlConnection.simple._

object SlickInsert {

  def run: Report = {
    printMe(Report("Inserting users and one account per user with one connection",
      for (i <- numberOfInserts) yield {
        printMe(reportLine(performWithTransactionN(i)(action(_))))
        //println(performInTransactionN(i)(action(_)))
      },
      Chronograph.Micros))
  }

  private final def action(implicit s:Session):Unit = {
    val id = insertUser insert newUser
     insertAccounts insert newPayAccount(id)
  }

  private final def newUser = MainTcUserRow(0, Some("test"), Some("test"), Some("test"), Some("test"))
  private final def newPayAccount(userId:Int) = PayTdAccountRow(0, Some(500), Some(200), userId)


}