package support

import scala.slick.driver.JdbcProfile

case class JdbcConnectionTemplate(url:String, username:String, password:String, driver:String)
// ^^ TODO misses connection properties

trait SlickProfile {
  val profile: JdbcProfile
  val simple:profile.simple.type = profile.simple
  type Session = scala.slick.jdbc.JdbcBackend#SessionDef
  object DynSession {
    @inline implicit def dynSession = scala.slick.jdbc.JdbcBackend.Database.dynamicSession
  }
}

trait TablesDefinition extends SlickProfile {
  val ddl: profile.DDL
}

trait SlickSupport extends SlickProfile { this: TablesDefinition =>

  import this.simple._

  val jdbc: JdbcConnectionTemplate

  final val database: Database =  Database.forURL( jdbc.url, jdbc.username, jdbc.password,null, jdbc.driver)

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


  // **** Schema management
  def createSchema = database withSession { s => ddl.create(s) }
  def destroySchema = database withSession { s => ddl.drop(s) }
  import scalaz._
  def inSchema[A](f: => A): \/[Throwable,A] = {
    import scala.util.control.Exception._
    createSchema
    val res = allCatch either f
    destroySchema
    res.fold(_.printStackTrace(), _ => ()) // TODO - this is a *HACK*
    \/.fromEither(res)
  }

  import scala.slick.model.codegen.{ SourceCodeGenerator => SCG }

  /** Generates the Slick model based on the database schema */
  def genCode = {
    SCG.main(Array(profile.getClass.getName, jdbc.driver, jdbc.url, ".", "xxx",jdbc.username, jdbc.password))
  }
  // ^^ TODO thinking again, it doesn't make much sense the code generator be in class that requires the existence of
  // TableDefinitions, isn't it?
}

trait StdSlickSupport extends SlickSupport with SlickProfile { this: TablesDefinition => }




