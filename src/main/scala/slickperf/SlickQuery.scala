package slickperf

import exec.TestHelper._
import exec._
import support.SlickInstances._
import SlickMySql._
import SlickMySql.simple._

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

  val queries = findUser(1).selectStatement

  private final def action(is:List[Long])(s:Session):Int = {
    is map { i =>
      findUserCompiled(i.toInt)
    }
    is.length
  }

  import scalaz._
  import Scalaz._
  import exec.Chronometer._

  def run(repetitions:NEL[Int]):ElapsedTimeOf[String, NEL[Chronon]] = {

    println(title)

    import scalaz._
    val allIds = (1 to repetitions.foldMap1(identity)).map(_.toLong).toList
    val res: NEL[ElapsedTimeOf[NEL[Int], NEL[Chronon]]] = for (i <- repetitions) yield {
      val ids = scala.util.Random.shuffle(allIds).take(i).toList
      printMe(performWithTransactionN(i)(action(ids))).nelnel
    }

    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)

  }


}