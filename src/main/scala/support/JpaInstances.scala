package support

/**
 * Created by pedrofurla on 31/03/14.
 */
object JpaInstances {
  object EclipseLinkJpa extends {
    val persistenceUnit="mysql-eclipselink"
  } with JpaConnection

  object HibernateJpa extends {
    val persistenceUnit="mysql-hibernate"
  } with JpaConnection


  object HibernateJpa2 extends {
    val persistenceUnit="jpa2-mysql-hibernate"
  } with JpaConnection

}