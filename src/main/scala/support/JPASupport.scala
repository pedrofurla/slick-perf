package support

import exec.TestHelper

/**
 * Created by pedrofurla on 31/03/14.
 */
object Jpa {
  object EclipseLinkJpa extends {
    val persistenceUnit="mysql-eclipselink"
  } with Jpa

  object HibernateJpa extends {
    val persistenceUnit="mysql-hibernate"
  } with Jpa


  object HibernateJpa2 extends {
    val persistenceUnit="jpa2-mysql-hibernate"
  } with Jpa

}
trait Jpa {

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

  import exec.Chronometer._
  import TestHelper._

  //type Perform[A,B] = B => EMAction[A] => ElapsedTimeOf[A]

  final def performInTransactionN: Int => EMAction[Unit] => ElapsedTimeOf[Int,Chronon] = (n:Int) => (action:EMAction[Unit]) =>
    inJpa {
      chronograph { const(n) compose inTransaction { em => repeatN(n)(action(em)) } }
    }

  final def performNInTransaction: Int => EMAction[Unit] => ElapsedTimeOf[Int,Chronon] = (n:Int) => (action:EMAction[Unit]) =>
    inJpa {
      chronograph { const(n) compose { em => repeatN(n) { inTransaction(action)(em) } } }
    }

  final def performWithTransactionN: Int => EMAction[Unit] => ElapsedTimeOf[Int,Chronon] = (n:Int) => (action:EMAction[Unit]) =>
    chronograph{ (n:Int) => withTransaction { em => repeatN(n)(action(em)) }; n }(n)

  final def performNWithTransaction: Int => EMAction[Unit] => ElapsedTimeOf[Int,Chronon] = (n:Int) => (action:EMAction[Unit]) =>
    chronograph{ (n:Int) => repeatN(n) { withTransaction(action) }; n  }(n)

  final def performInTransaction[A,B]: B => EMAction[A] => ElapsedTimeOf[A,Chronon] = (n:B) => (action:EMAction[A]) =>
    inJpa { em =>
      chronograph[B,A] { n => inTransaction{em => action(em)}(em) }(n)
    }

  final def performWithTransaction[A,B]: B => EMAction[A] => ElapsedTimeOf[A,Chronon] = (n:B) => (action:EMAction[A]) =>
    chronograph[B,A] { n => withTransaction { em => action(em) } }(n)

}
