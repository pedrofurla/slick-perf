package slickperf

import exec.TestHelper._
import exec._
import support.SlickInstances._
import SlickMySql._
import SlickMySql.simple._

object SlickInsertCompany extends DbRun {

  val title = "Slick Inserting companies" // + "  with one connection"

  private final val insertCompany = Companies returning Companies.map(_.id)

  val queries = List(insertCompany.insertStatement) // to keep a list of the queries, also

  private final def action(implicit s:Session):Unit = {
    val id = insertCompany insert newCompany
  }

  private final def newCompany = new CompanyRow(0,"Street","Name")

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