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
        val ids = allIds.take(i).toList

        printMe(reportLine(performInTransaction(i)(action(ids))))
        // hurts the performance horribly
        //printMe(reportLine(performWithTransaction(i)(action(ids))))
      },
      Chronograph.Micros))
  }

  private final def action(ids:List[Long])(em:EntityManager):Int = {
    val query = em.createQuery("from MainUser u where u.id = ?1")
    ids.map{ i =>
      import scala.collection.JavaConversions._
      val users = query.setParameter(1, i).getResultList()

      val result = for {
        u <- users
        acc <- u.asInstanceOf[MainUser].getPayAccountList
      } yield (u, acc)

      // force loading
      for (i <- result) yield { 2*i._2.getAmount }
    }
    ids.length
  }




}