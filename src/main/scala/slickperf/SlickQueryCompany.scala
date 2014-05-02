package slickperf

import exec.TestHelper._
import exec._

object SlickQueryCompany extends DbRun  {
  
  import support.SlickInstances._
  import SlickMySql._
  import SlickMySql.simple._
  
  val title = "Slick Querying companies at random"

  def findCompany(id: Column[Int]) = Companies.filter(_.id === id)

  val findCompanyCompiled = Compiled(findCompany _)

  val queries = List(findCompany(1).selectStatement)

  private final def action(is:List[Long])(s:Session):Int = {
    is map { i =>
      findCompanyCompiled(i.toInt).run(s)
      1
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
      printMe(performWithTransaction(i)(action(ids))).nelnel
    }

    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)

  }


}