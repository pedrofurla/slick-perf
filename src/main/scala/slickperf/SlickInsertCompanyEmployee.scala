package slickperf

import exec.TestHelper._
import exec._
import support.SlickInstances._
import SlickMySql._
import SlickMySql.simple._

object SlickInsertCompanyEmployee extends DbRun {

  val title = "Slick Inserting companies and employees" // + "  with one connection"

  private final val insertCompany = Companies returning Companies.map(_.id)
  private final val insertEmployee = Employees //returning Employees.map(_.id) // PayTdAccount.insertInvoker
  val queries = List(insertCompany.insertStatement, insertEmployee.insertStatement) // to keep a list of the queries, also

  private final def action(implicit s:Session):Unit = {
    val id = insertCompany insert newCompany
    val reminder = 1 // (id % 5) + 1
    //for(i <- 1 to reminder) yield insertEmployee insert newEmployee(id)
    insertEmployee ++= (for(i <- 1 to reminder) yield  newEmployee(id))
  }

  private final def newCompany = new CompanyRow(0,"Street","Name")
  private final def newEmployee(companyId:Int) = new EmployeeRow(0,"Name","Phone#",companyId)

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