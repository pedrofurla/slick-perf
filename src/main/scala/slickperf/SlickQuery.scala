package slickperf

import exec.TestHelper._
import exec._
import exec.Reports._
import MySqlConnection._
import MySqlConnection.simple._

object SlickQuery  {

  def run: Report = {
    printMe(Report(
      "Querying users and its accounts",
      for (i <- numberOfInserts) yield {
        val ids = scala.util.Random.shuffle(allIds).take(i).toList
        printMe(reportLine(performWithTransaction(i)(action(ids))))
        //println(performInTransactionN(i)(action(_)))
      },
      Chronograph.Micros))
  }

  /*
    def performWithTransaction[A,B](n:B)(action:SlickAction[A]): ElapsedTimeOf[A] =
    withTransaction {
      import DynSession._
      micros[B,A]( n =>  action(dynSession) )(n)
    }
   */

  private final def action(is:List[Long])(s:Session):Int = {
    is map { i =>
      findUserCompiled(i.toInt)
    }
    is.length
  }



}