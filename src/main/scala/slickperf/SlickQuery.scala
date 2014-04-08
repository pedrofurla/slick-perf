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
        printMe(reportLine(performWithTransactionN(i)(action(ids))))
        //println(performInTransactionN(i)(action(_)))
      },
      Chronograph.Micros))
  }

  private final def action(is:List[Long])(s:Session):Unit = {
    is map { i =>
      findUserCompiled(i.toInt)
    }
  }



}