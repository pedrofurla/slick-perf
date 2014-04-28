package slickperf

import exec.TestHelper._
import exec._
//import exec.Reports._
import MySqlConnection._
import MySqlConnection.simple._
//import exec.Reports.Report

object SlickQuery extends DbRun  {

  val title = "Slick Querying users and its accounts"

/*  def run(repetitions:List[Int]): Report = {
    printMe(Report(
      title,
      for (i <- repetitions) yield {
        val ids = scala.util.Random.shuffle(allIds).take(i).toList
        printMe(reportLine(performWithTransaction(i)(action(ids))))
        //println(performInTransactionN(i)(action(_)))
      },
      Chronograph.Micros))
  }*/

  def findUser(id: Column[Int]) = for {
    account <- PayTdAccount
    user <- account.mainTcUserFk if user.id === id
  } yield (user, account)

  val findUserCompiled = Compiled(findUser _)

  private final def action(is:List[Long])(s:Session):Int = {
    is map { i =>
      findUserCompiled(i.toInt)
    }
    is.length
  }

  import scalaz._
  import Scalaz._
  import exec.Chronograph2._

  def run2(repetitions:NonEmptyList[Int]):ElapsedTimeOf[String, NonEmptyList[Chronon]] = {

    def performWithTransactionN[A](n:Int)(action:SlickAction[Unit]) =
      withTransaction {
        import DynSession._
        chronographN(n)( action )(dynSession)
      }

    println(title)

    val res: NonEmptyList[ElapsedTimeOf[NonEmptyList[Int], NonEmptyList[Chronon]]] = for (i <- repetitions) yield {
      val ids = scala.util.Random.shuffle(allIds).take(i).toList
      printMe(performWithTransactionN(i)(action(ids))).nelnel
    }

    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)

  }


}