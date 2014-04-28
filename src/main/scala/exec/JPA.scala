package exec

/**
 * Created by pedrofurla on 31/03/14.
 */
object JPA {
  object EclipseLinkJPA extends {
    val persistenceUnit="mysql-eclipselink"
  } with JPA

  object HibernateLinkJPA extends {
    val persistenceUnit="mysql-hibernate"
  } with JPA
}
trait JPA {

  import javax.persistence.{Persistence, EntityManager, EntityManagerFactory}

  val persistenceUnit:String

  final val emFactory: EntityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit)
  final def newEntityManager = emFactory.createEntityManager()

  final val defaultEm:EM = newEntityManager

  final type EM = EntityManager
  final type EMAction[A] = EM => A

  final def inJpa[A](f: EMAction[A]):A = {
    val em = newEntityManager
    val res = f(em)
    em.close
    res
  }
  final def withJpa[A] = (f: EMAction[A]) => f(defaultEm)

  final def inTransaction[A](f: EMAction[A]): EMAction[A] = { em =>
    em.getTransaction().begin();
    val b = f(em)
    em.getTransaction().commit();
    b
  }
  final def withTransaction[A]: EMAction[A] => A = withJpa[A] compose inTransaction[A]

  import exec.Chronograph._
  import TestHelper._

  //type Perform[A,B] = B => EMAction[A] => ElapsedTimeOf[A]

  final def performInTransactionN: Int => EMAction[Unit] => ElapsedTimeOf[Int] = (n:Int) => (action:EMAction[Unit]) =>
    inJpa {
      micros { const(n) compose inTransaction { em => repeatN(n)(action(em)) } }
    }

  final def performNInTransaction: Int => EMAction[Unit] => ElapsedTimeOf[Int] = (n:Int) => (action:EMAction[Unit]) =>
    inJpa {
      micros { const(n) compose { em => repeatN(n) { inTransaction(action)(em) } } }
    }

  final def performWithTransactionN: Int => EMAction[Unit] => ElapsedTimeOf[Int] = (n:Int) => (action:EMAction[Unit]) =>
    micros{ (n:Int) => withTransaction { em => repeatN(n)(action(em)) }; n }(n)

  final def performNWithTransaction: Int => EMAction[Unit] => ElapsedTimeOf[Int] = (n:Int) => (action:EMAction[Unit]) =>
    micros{ (n:Int) => repeatN(n) { withTransaction(action) }; n  }(n)

  final def performInTransaction[A,B]: B => EMAction[A] => ElapsedTimeOf[A] = (n:B) => (action:EMAction[A]) =>
    inJpa { em =>
      micros[B,A] { n => inTransaction{em => action(em)}(em) }(n)
    }

  final def performWithTransaction[A,B]: B => EMAction[A] => ElapsedTimeOf[A] = (n:B) => (action:EMAction[A]) =>
    micros[B,A] { n => withTransaction { em => action(em) } }(n)

}
