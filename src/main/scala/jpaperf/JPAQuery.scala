package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec.Chronograph
import exec.Reports._
import exec.JPA._

object JPAQuery {
  def run() {
    printMe(Report(
      "Querying users and its accounts",
      for (i <- numberOfInserts) yield {
        val ids = scala.util.Random.shuffle(allIds).take(i).toList
        printMe(reportLine(performWithTransactionN(i)(action(ids))))
        //println(performInTransactionN(i)(action(_)))
      },
      Chronograph.Micros))
  }

  private final def action(ids:List[Long])(em:EntityManager):Unit = {
    ids.map{ i =>
      val users = em.createQuery("from MainUser u where u.id = ?1").setParameter(1, i).getResultList()
      import scala.collection.JavaConversions._

      val result = for {
        u <- users
        acc <- u.asInstanceOf[MainUser].getPayAccountList
      } yield (u, acc)

      // force loading
      for (i <- result) yield { 2*i._2.getAmount }
    }
  }




}