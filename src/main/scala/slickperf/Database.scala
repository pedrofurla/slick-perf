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
  this: ConnectionTemplate with SlickProfile with Tables =>

  import simple._
  final val database: Database =  Database.forURL( url, username, password,null, driver)

  def createSchema = database withSession { s => ddl.create(s) }
  def destroySchema = database withSession { s => ddl.drop(s) }
  def inSchema(f: => Unit):Unit = {
    import scala.util.control.Exception._
    createSchema
    val res = allCatch either f
    destroySchema
    res.fold(x => x.printStackTrace(), _ => {})
  }

  type SlickAction[A] = Session => A

  def inDb[A](f:SlickAction[A]) = database withSession f
  def withDb[A](a: => A) = database withDynSession a
  def inTransaction[A](f:SlickAction[A]):A = database withTransaction f
  def withTransaction[A](a: => A):A = database withDynTransaction a

  import exec.Chronograph._
  import exec.TestHelper.{const,repeatN}
  def performWithTransactionN[A](n:Int)(action:SlickAction[Unit]): ElapsedTimeOf[Int] =
    withTransaction {
      import DynSession._
      micros[Int,Int]( n => { repeatN(n)(action(dynSession)); n } )(n)
    }

  def performInTransactionN(n:Int)(action:SlickAction[Unit]): ElapsedTimeOf[Int] =
     inTransaction {
       micros { const(n) compose { s => repeatN(n)(action(s)) } }
     }

  def performWithTransaction[A,B](n:B)(action:SlickAction[A]): ElapsedTimeOf[A] =
    withTransaction {
      import DynSession._
      micros[B,A]( n =>  action(dynSession) )(n)
    }

  def performInTransaction[A,B](n:B)(action:SlickAction[A]): ElapsedTimeOf[A] =
     inTransaction { s =>
       micros[B,A] { n => action(s) }(n)
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

trait CommonConnection extends SlickProfile with DBConnection with Tables {
  this: ConnectionTemplate =>

  import simple._
  val insertUser = MainTcUser returning MainTcUser.map(_.id)
  insertUser.insertStatement

  val insertAccounts = PayTdAccount returning PayTdAccount.map(_.id) // PayTdAccount.insertInvoker
  insertAccounts.insertStatement

  def findUser(id: Column[Int]) = for {
    account <- PayTdAccount
    user <- account.mainTcUserFk if user.id === id
  } yield (user, account)

  val findUserCompiled = Compiled(findUser _)
}

object MySqlConnection extends {
  val profile = scala.slick.driver.MySQLDriver
} with MySqlConnection with CommonConnection

object PostgresConnection extends {
  val profile = scala.slick.driver.PostgresDriver
} with PostgresConnection with CommonConnection

