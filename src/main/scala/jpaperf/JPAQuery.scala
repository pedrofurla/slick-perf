package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec.DbRun

class JPAQuery(jpa:exec.JPA) extends DbRun {

  import jpa._

  val title = s"JPA ${jpa.persistenceUnit} Querying users and its accounts"

  private final def action(ids:List[Long])(em:EntityManager):Int = {
    val query = em.createQuery("from MainUser u where u.id = ?1")
    //val query = em.createQuery("from MainUser u LEFT JOIN FETCH PayAccount where u.id = ?1")

    //println(query.unwrap(classOf[org.eclipse.persistence.jpa.JpaQuery[_]]).getDatabaseQuery.getSQLString)

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
  import exec.Chronograph2._
  import scalaz._
  import Scalaz._

  def run2(repetitions:NonEmptyList[Int]):ElapsedTimeOf[String, NonEmptyList[Chronon]] = {
    println(title)

    import scalaz._
    val allIds = (1 to repetitions.foldMap1(identity)).map(_.toLong).toList
    val res: NonEmptyList[ElapsedTimeOf[NonEmptyList[Int], NonEmptyList[Chronon]]] = for (i <- repetitions) yield {
      val ids = allIds.take(i).toList
      printMe(performInTransaction(i)(action(ids))).nelnel
    }
    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)
  }


}