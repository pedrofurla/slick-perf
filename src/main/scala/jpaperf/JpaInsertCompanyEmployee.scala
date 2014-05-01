package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec._
import support.JpaConnection

class JpaInsertCompanyEmployee(jpa:JpaConnection) extends DbRun {
  import jpa._

  val title = s"JPA ${jpa.persistenceUnit} Inserting companies and employees" // + " with one connection"

  import jpa2._
  import Entities._

  private final def action(em:EntityManager):Unit = {
    val c = newCompany
    em persist c
    val reminder = 1 //(c.getId % 5) + 1
    for(i <- 1 to reminder) {
      val e = newEmployee(c) // TODO is there some batch we can do here?
      em persist e
    }
  }

  private final def newCompany:Company = {
    val c = new Company
    c.setName("zlaja")
    c.setAddress("zlaja")
    c
  }

  private final def newEmployee(company:Company) = {
    val e = new Employee
    e.setName("name")
    e.setPhone("phone#")
    e.setCompany(company)
    //user.getPayAccountList.add(e)
    e
  }

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