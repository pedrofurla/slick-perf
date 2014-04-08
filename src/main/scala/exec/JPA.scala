package exec

/**
 * Created by pedrofurla on 31/03/14.
 */
object JPA {
  import javax.persistence.{Persistence, EntityManager}

  def newFactory = Persistence.createEntityManagerFactory("slickperf-persistence-mysql")
  def newEntityManager = {
    newFactory.createEntityManager()
  }

  val defaultEm:EM = newEntityManager

  type EM = EntityManager
  type EMAction[A] = EM => A

  def inJpa[A] = (f: EMAction[A]) => f(newEntityManager)
  def withJpa[A] = (f: EMAction[A]) => f(defaultEm)

  def inTransaction[A](f: EMAction[A]): EMAction[A] = { em =>
    em.getTransaction().begin();
    val b = f(em)
    em.getTransaction().commit();
    b
  }
  def withTransaction[A]: EMAction[A] => A = withJpa[A] compose inTransaction[A]

  import exec.Chronograph._
  import TestHelper._

  def performInTransactionN(n:Int)(action:EMAction[Unit]): ElapsedTimeOf[Int] =
    inJpa {
      micros { const(n) compose inTransaction { em => repeatN(n)(action(em))  }  }
    }

  def performNInTransaction(n:Int)(action:EMAction[Unit]): ElapsedTimeOf[Int] =
    inJpa {
      micros { const(n) compose { em => repeatN(n) { inTransaction(action)(em) } } }
    }

  def performWithTransactionN(n:Int)(action:EMAction[Unit]): ElapsedTimeOf[Int] =
    micros{ (n:Int) => withTransaction { em => repeatN(n)(action(em))  }; n  }(n)

  def performNWithTransaction(n:Int)(action:EMAction[Unit]): ElapsedTimeOf[Int] =
    micros{ (n:Int) => repeatN(n) { withTransaction(action) }; n  }(n)

}
