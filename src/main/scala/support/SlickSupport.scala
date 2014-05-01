package support

import scala.slick.driver.JdbcProfile

case class ConnectionTemplate(url:String, username:String, password:String, driver:String)

trait SlickProfile {
  val profile: JdbcProfile
  val simple:profile.simple.type = profile.simple
  type Session = scala.slick.jdbc.JdbcBackend#SessionDef
  object DynSession {
    @inline implicit def dynSession = scala.slick.jdbc.JdbcBackend.Database.dynamicSession
  }
}

trait SlickConnection {
  this: SlickProfile =>

  val dbConnection: ConnectionTemplate
  
  import dbConnection._
  
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
  /* RepeatN should return a list or nel and ElapsedTimeOf[Nel[B],Chronon] will make more sense
  def performWithTransactionN2[A,B](n:Int)(action:SlickAction[B]): ElapsedTimeOf[B,Chronon] =
      withTransaction {
        import DynSession._
        chronograph[Int,B]( n => { repeatN(n)(action(dynSession)); } )(n)
      }
  */

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

trait TablesDefinition extends SlickProfile {
  val ddl: profile.DDL
}

trait CommonConnection extends SlickProfile with SlickConnection {
  this: TablesDefinition =>

  import simple._

  def createSchema = database withSession { s => ddl.create(s) }
  def destroySchema = database withSession { s => ddl.drop(s) }
  import scalaz._
  def inSchema[A](f: => A): \/[Throwable,A] = {
    import scala.util.control.Exception._
    createSchema
    val res = allCatch either f
    destroySchema
    res.fold(_.printStackTrace(), _ => ())
    \/.fromEither(res)
  }
}

object SlickConnection {

  val mySqlConnection = ConnectionTemplate (
     url = "jdbc:mysql://localhost:3306/slickperf",
     username = "root",
     password = "",
     driver = "com.mysql.jdbc.Driver"
  )

  val postgresConnection = ConnectionTemplate (
     url = "jdbc:postgresql://127.0.0.1:5432/slickperf",
     username = "root",
     password = "",
     driver = "org.postgresql.Driver"
  )

  val mySqlConnection2 = ConnectionTemplate (
     url = "jdbc:mysql://localhost:3306/jpa2",
     username = "root",
     password = "",
     driver = "com.mysql.jdbc.Driver"
  )
}

import SlickConnection._
object SlickMySql extends {
  val dbConnection = mySqlConnection
  val profile = scala.slick.driver.MySQLDriver
} with CommonConnection with slickperf.Tables

object SlickPostgres extends {
  val dbConnection = postgresConnection  
  val profile = scala.slick.driver.PostgresDriver
} with CommonConnection  with slickperf.Tables

object SlickMySql2 extends {
  val dbConnection = mySqlConnection2
  val profile = scala.slick.driver.MySQLDriver
} with  CommonConnection with slickperf.Tables2
