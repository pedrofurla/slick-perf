package slickperf

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables { self: DBConnection with SlickProfile =>

  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}



  /** DDL for all tables. Call .create to execute. */
  lazy val ddl:self.profile.DDL = MainTcUser.ddl ++ PayTdAccount.ddl ++ PayTdiAccountItem.ddl
  
  /** Entity class storing rows of table MainTcUser
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param surname Database column surname 
   *  @param username Database column username 
   *  @param password Database column password  */
  case class MainTcUserRow(id: Int, name: Option[String], surname: Option[String], username: Option[String], password: Option[String])
  /** GetResult implicit for fetching MainTcUserRow objects using plain SQL queries */
  implicit def GetResultMainTcUserRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[MainTcUserRow] = GR{
    prs => import prs._
    MainTcUserRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String]))
  }

  /** Table description of table main_tc_user. Objects of this class serve as prototypes for rows in queries. */
  class MainTcUser(tag: Tag) extends Table[MainTcUserRow](tag, "main_tc_user") {
    def * = (id, name, surname, username, password) <> (MainTcUserRow.tupled, MainTcUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name, surname, username, password).shaped.<>({r=>import r._; _1.map(_=> MainTcUserRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    /** Database column surname  */
    val surname: Column[Option[String]] = column[Option[String]]("surname")
    /** Database column username  */
    val username: Column[Option[String]] = column[Option[String]]("username")
    /** Database column password  */
    val password: Column[Option[String]] = column[Option[String]]("password")
  }
  /** Collection-like TableQuery object for table MainTcUser */
  lazy val MainTcUser = new TableQuery(tag => new MainTcUser(tag))
  
  /** Entity class storing rows of table PayTdAccount
   *  @param id Database column id PrimaryKey
   *  @param amount Database column amount 
   *  @param reserved Database column reserved 
   *  @param userId Database column user_id  */
  case class PayTdAccountRow(id: Int, amount: Option[Double], reserved: Option[Double], userId: Int)
  /** GetResult implicit for fetching PayTdAccountRow objects using plain SQL queries */
  implicit def GetResultPayTdAccountRow(implicit e0: GR[Int], e1: GR[Option[Double]]): GR[PayTdAccountRow] = GR{
    prs => import prs._
    PayTdAccountRow.tupled((<<[Int], <<?[Double], <<?[Double], <<[Int]))
  }
  /** Table description of table pay_td_account. Objects of this class serve as prototypes for rows in queries. */
  class PayTdAccount(tag: Tag) extends Table[PayTdAccountRow](tag, "pay_td_account") {
    def * = (id, amount, reserved, userId) <> (PayTdAccountRow.tupled, PayTdAccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, amount, reserved, userId.?).shaped.<>({r=>import r._; _1.map(_=> PayTdAccountRow.tupled((_1.get, _2, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    /** Database column amount  */
    val amount: Column[Option[Double]] = column[Option[Double]]("amount")
    /** Database column reserved  */
    val reserved: Column[Option[Double]] = column[Option[Double]]("reserved")
    /** Database column user_id  */
    val userId: Column[Int] = column[Int]("user_id")
    
    /** Foreign key referencing MainTcUser (database name fk_PAY_TD_ACCOUNT_MAIN_TC_USER) */
    lazy val mainTcUserFk = foreignKey("fk_PAY_TD_ACCOUNT_MAIN_TC_USER", userId, MainTcUser)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PayTdAccount */
  lazy val PayTdAccount = new TableQuery(tag => new PayTdAccount(tag))
  
  /** Entity class storing rows of table PayTdiAccountItem
   *  @param id Database column id PrimaryKey
   *  @param amount Database column amount 
   *  @param transactionType Database column transaction_type 
   *  @param inserted Database column inserted 
   *  @param accountId Database column account_id  */
  case class PayTdiAccountItemRow(id: Int, amount: Option[String], transactionType: Option[Int], inserted: Option[java.sql.Timestamp], accountId: Int)
  /** GetResult implicit for fetching PayTdiAccountItemRow objects using plain SQL queries */
  implicit def GetResultPayTdiAccountItemRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[java.sql.Timestamp]]): GR[PayTdiAccountItemRow] = GR{
    prs => import prs._
    PayTdiAccountItemRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[java.sql.Timestamp], <<[Int]))
  }
  /** Table description of table pay_tdi_account_item. Objects of this class serve as prototypes for rows in queries. */
  class PayTdiAccountItem(tag: Tag) extends Table[PayTdiAccountItemRow](tag, "pay_tdi_account_item") {
    def * = (id, amount, transactionType, inserted, accountId) <> (PayTdiAccountItemRow.tupled, PayTdiAccountItemRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, amount, transactionType, inserted, accountId.?).shaped.<>({r=>import r._; _1.map(_=> PayTdiAccountItemRow.tupled((_1.get, _2, _3, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column amount  */
    val amount: Column[Option[String]] = column[Option[String]]("amount")
    /** Database column transaction_type  */
    val transactionType: Column[Option[Int]] = column[Option[Int]]("transaction_type")
    /** Database column inserted  */
    val inserted: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("inserted")
    /** Database column account_id  */
    val accountId: Column[Int] = column[Int]("account_id")
    
    /** Foreign key referencing PayTdAccount (database name fk_PAY_TDI_ACCOUNT_PAY_TD_ACCOUNT1) */
    lazy val payTdAccountFk = foreignKey("fk_PAY_TDI_ACCOUNT_PAY_TD_ACCOUNT1", accountId, PayTdAccount)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PayTdiAccountItem */
  lazy val PayTdiAccountItem = new TableQuery(tag => new PayTdiAccountItem(tag))
}