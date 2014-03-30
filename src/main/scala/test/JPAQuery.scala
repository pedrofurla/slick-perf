package client.test

import jpaperf.MainUser
import javax.persistence.Persistence
import TestHelper._

object JPAQuery {

  def main(args: Array[String]) {
    execute(10) {
      test()
    }
  }

  def test() {
    val factory = Persistence.createEntityManagerFactory("slickperf-persistence")
    val em = factory.createEntityManager()
    val duration = chronometer {
      em.getTransaction().begin();

      val users = em.createQuery("from MainUser u where u.id = ?1").setParameter(1, 1).getResultList()
      import scala.collection.JavaConversions._

      val result = for {
        u <- users
        acc <- u.asInstanceOf[MainUser].getPayAccountList
      } yield (u, acc)

      for (i <- result)
        println(s"u: $i._1, acc: $i._2")

      em.getTransaction().commit();
    }
    printTime(duration)

  }

}