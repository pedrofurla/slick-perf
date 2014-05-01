package slickperf.old

import exec.TestHelper._
import exec._
import support.SlickInstances._
import SlickMySqlOld._
import SlickMySqlOld.simple._
import exec.Chronometer.Chronon

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

  import exec.Chronometer._
  import scalaz._
  import Scalaz._

  def run(repetitions:NEL[Int]):ElapsedTimeOf[String, NEL[Chronon]] = {
    println(title)
    val res: NEL[ElapsedTimeOf[NEL[Int], NEL[Chronon]]] = for (i <- repetitions) yield {
      printMe(performWithTransactionN(i)(action(_))).nelnel
    }

    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)
  }



}