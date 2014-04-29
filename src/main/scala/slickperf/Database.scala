package slickperf

import scala.slick.driver.JdbcProfile

trait ConnectionTemplate {
  val url:String
  val username:String
  val password:String
  val driver:String
}

trait SlickProfile {
  val profile: JdbcProfile
  val simple:profile.simple.type = profile.simple
  type Session = scala.slick.jdbc.JdbcBackend#SessionDef
  object DynSession {
    @inline implicit def dynSession = scala.slick.jdbc.JdbcBackend.Database.dynamicSession
  }
}

trait DBConnection {
  this: ConnectionTemplate with SlickProfile =>

  import simple._
  final val database: Database =  Database.forURL( url, username, password,null, driver)

  import scala.slick.model.codegen.{ SourceCodeGenerator => SCG }

  /** Generates the Slick model based on the database schema */
  def genCode = {
    SCG.main(Array(profile.getClass.getName, driver, url, ".", "xxx",username, password))
  }

  type SlickAction[A] = Session => A

  def inDb[A](f:SlickAction[A]) = database withSession f
  def withDb[A](a: => A) = database withDynSession a
  def inTransaction[A](f:SlickAction[A]):A = database withTransaction f
  def withTransaction[A](a: => A):A = database withDynTransaction a

  import exec.Chronometer._
  import exec.TestHelper.{const,repeatN}
  def performWithTransactionN[A](n:Int)(action:SlickAction[Unit]): ElapsedTimeOf[Int,Chronon] =
    withTransaction {
      import DynSession._
      chronograph[Int,Int]( n => { repeatN(n)(action(dynSession)); n } )(n)
    }

  def performInTransactionN(n:Int)(action:SlickAction[Unit]): ElapsedTimeOf[Int,Chronon] =
     inTransaction {
       chronograph { const(n) compose { s => repeatN(n)(action(s)) } }
     }

  def performWithTransaction[A,B](n:B)(action:SlickAction[A]): ElapsedTimeOf[A,Chronon] =
    withTransaction {
      import DynSession._
      chronograph[B,A]( n =>  action(dynSession) )(n)
    }

  def performInTransaction[A,B](n:B)(action:SlickAction[A]): ElapsedTimeOf[A,Chronon] =
     inTransaction { s =>
       chronograph[B,A] { n => action(s) }(n)
     }

}

trait CommonConnection extends SlickProfile with DBConnection with Tables {
  this: ConnectionTemplate =>

  import simple._

  def createSchema = database withSession { s => ddl.create(s) }
  def destroySchema = database withSession { s => ddl.drop(s) }
  import scalaz._
  def inSchema[A](f: => A): \/[Throwable,A] = {
    import scala.util.control.Exception._
    createSchema
    val res = allCatch either f
    destroySchema

    \/.fromEither(res)
  }
}

trait MySqlConnection extends ConnectionTemplate {
  val url = "jdbc:mysql://localhost:3306/slickperf"
  val username = "root"
  val password = ""
  val driver = "com.mysql.jdbc.Driver"
}
trait PostgresConnection extends ConnectionTemplate {
  val url = "jdbc:postgresql://127.0.0.1:5432/slickperf"
  val username = "root"
  val password = ""
  val driver = "org.postgresql.Driver"
}

object MySqlConnection extends {
  val profile = scala.slick.driver.MySQLDriver
} with MySqlConnection with CommonConnection

object PostgresConnection extends {
  val profile = scala.slick.driver.PostgresDriver
} with PostgresConnection with CommonConnection


trait MySqlConnection2 extends ConnectionTemplate {
  val url = "jdbc:mysql://localhost:3306/jpa2"
  val username = "root"
  val password = ""
  val driver = "com.mysql.jdbc.Driver"
}
object MySqlConnection2 extends {
  val profile = scala.slick.driver.MySQLDriver
} with SlickProfile with MySqlConnection2 with DBConnection with Tables2
