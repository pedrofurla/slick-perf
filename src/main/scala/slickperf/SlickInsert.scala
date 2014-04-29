package slickperf

import exec.TestHelper._
import exec._
//import exec.Reports._
import MySqlConnection._
import MySqlConnection.simple._
import exec.Chronograph2.{Chronon, ElapsedTimeOf}

object SlickInsert extends DbRun {

  val title = "Slick Inserting users and one account per user" // + "  with one connection"

  private val insertUser = MainTcUser returning MainTcUser.map(_.id)
  private val insertAccounts = PayTdAccount returning PayTdAccount.map(_.id) // PayTdAccount.insertInvoker
  val queries = List(insertUser.insertStatement, insertAccounts.insertStatement) // to keep a list of the queries, also

  private final def action(implicit s:Session):Unit = {
    val id = insertUser insert newUser
    insertAccounts insert newPayAccount(id)
  }

  private final def newUser = MainTcUserRow(0, Some("test"), Some("test"), Some("test"), Some("test"))
  private final def newPayAccount(userId:Int) = PayTdAccountRow(0, Some(500), Some(200), userId)

  import exec.Chronograph2._
  import scalaz._
  import Scalaz._

  def run2(repetitions:NonEmptyList[Int]):ElapsedTimeOf[String, NonEmptyList[Chronon]] = {
    println(title)
    val res: NonEmptyList[ElapsedTimeOf[NonEmptyList[Int], NonEmptyList[Chronon]]] = for (i <- repetitions) yield {
      printMe(performWithTransactionN(i)(action(_))).nelnel
    }

    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)
  }



}