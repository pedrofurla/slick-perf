package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec._
import support.JpaConnection

class JpaInsertCompany(jpa:JpaConnection) extends DbRun {
  import jpa._

  val title = s"JPA ${jpa.persistenceUnit} Inserting companies" // + " with one connection"

  import Entities._

  private final def action(em:EntityManager):Unit = {
    val c = newCompany
    em persist c
  }

  private final def newCompany:Company = {
    val c = new Company
    c.setName("zlaja")
    c.setAddress("zlaja")
    c
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