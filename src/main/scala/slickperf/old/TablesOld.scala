package slickperf.old

import support.{TablesDefinition, SlickSupport, SlickProfile}

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait TablesOld extends TablesDefinition { self: SlickSupport with SlickProfile =>

  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}

  lazy val ddl:self.profile.DDL = MainTcUser.ddl ++ PayTdAccount.ddl ++ PayTdiAccountItem.ddl
  
  case class MainTcUserRow(id: Int, name: Option[String], surname: Option[String], username: Option[String], password: Option[String])

  class MainTcUser(tag: Tag) extends Table[MainTcUserRow](tag, "main_tc_user") {
    def * = (id, name, surname, username, password) <> (MainTcUserRow.tupled, MainTcUserRow.unapply)
    def ? = (id.?, name, surname, username, password).shaped.<>({r=>import r._; _1.map(_=> MainTcUserRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    val name: Column[Option[String]] = column[Option[String]]("name")
    val surname: Column[Option[String]] = column[Option[String]]("surname")
    val username: Column[Option[String]] = column[Option[String]]("username")
    val password: Column[Option[String]] = column[Option[String]]("password")
  }

  lazy val MainTcUser = new TableQuery(tag => new MainTcUser(tag))


  case class PayTdAccountRow(id: Int, amount: Option[Double], reserved: Option[Double], userId: Int)

  class PayTdAccount(tag: Tag) extends Table[PayTdAccountRow](tag, "pay_td_account") {
    def * = (id, amount, reserved, userId) <> (PayTdAccountRow.tupled, PayTdAccountRow.unapply)
    def ? = (id.?, amount, reserved, userId.?).shaped.<>({r=>import r._; _1.map(_=> PayTdAccountRow.tupled((_1.get, _2, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val amount: Column[Option[Double]] = column[Option[Double]]("amount")
    val reserved: Column[Option[Double]] = column[Option[Double]]("reserved")
    val userId: Column[Int] = column[Int]("user_id")
    
    lazy val mainTcUserFk = foreignKey("fk_PAY_TD_ACCOUNT_MAIN_TC_USER", userId, MainTcUser)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }

  lazy val PayTdAccount = new TableQuery(tag => new PayTdAccount(tag))

  case class PayTdiAccountItemRow(id: Int, amount: Option[String], transactionType: Option[Int], inserted: Option[java.sql.Timestamp], accountId: Int)

  class PayTdiAccountItem(tag: Tag) extends Table[PayTdiAccountItemRow](tag, "pay_tdi_account_item") {
    def * = (id, amount, transactionType, inserted, accountId) <> (PayTdiAccountItemRow.tupled, PayTdiAccountItemRow.unapply)
    def ? = (id.?, amount, transactionType, inserted, accountId.?).shaped.<>({r=>import r._; _1.map(_=> PayTdiAccountItemRow.tupled((_1.get, _2, _3, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    val amount: Column[Option[String]] = column[Option[String]]("amount")
    val transactionType: Column[Option[Int]] = column[Option[Int]]("transaction_type")
    val inserted: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("inserted")
    val accountId: Column[Int] = column[Int]("account_id")
    
    lazy val payTdAccountFk = foreignKey("fk_PAY_TDI_ACCOUNT_PAY_TD_ACCOUNT1", accountId, PayTdAccount)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }

  lazy val PayTdiAccountItem = new TableQuery(tag => new PayTdiAccountItem(tag))

  implicit def GetResultMainTcUserRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[MainTcUserRow] = GR{
    prs => import prs._
    MainTcUserRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  implicit def GetResultPayTdAccountRow(implicit e0: GR[Int], e1: GR[Option[Double]]): GR[PayTdAccountRow] = GR{
    prs => import prs._
    PayTdAccountRow.tupled((<<[Int], <<?[Double], <<?[Double], <<[Int]))
  }
  implicit def GetResultPayTdiAccountItemRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[java.sql.Timestamp]]): GR[PayTdiAccountItemRow] = GR{
    prs => import prs._
    PayTdiAccountItemRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[java.sql.Timestamp], <<[Int]))
  }
}