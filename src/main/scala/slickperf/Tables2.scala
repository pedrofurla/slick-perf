package slickperf

import scala.slick.model.ForeignKeyAction
import scala.slick.lifted

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables2 { self: DBConnection with SlickProfile =>

  import profile.simple._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Client.ddl ++ Company.ddl ++ CompanyClients.ddl ++ Employee.ddl
  
  /** Entity class storing rows of table Client
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param address Database column address 
   *  @param name Database column name  */
  case class ClientRow(id: Int, address: Option[String], name: String)
  /** GetResult implicit for fetching ClientRow objects using plain SQL queries */
  implicit def GetResultClientRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[String]): GR[ClientRow] = GR{
    prs => import prs._
    ClientRow.tupled((<<[Int], <<?[String], <<[String]))
  }
  /** Table description of table client. Objects of this class serve as prototypes for rows in queries. */
  class Client(tag: Tag) extends Table[ClientRow](tag, "client") {
    def * = (id, address, name) <> (ClientRow.tupled, ClientRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, address, name.?).shaped.<>({r=>import r._; _1.map(_=> ClientRow.tupled((_1.get, _2, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column address  */
    val address: Column[Option[String]] = column[Option[String]]("address")
    /** Database column name  */
    val name: Column[String] = column[String]("name")
  }
  /** Collection-like TableQuery object for table Client */
  lazy val Client = new TableQuery(tag => new Client(tag))
  
  /** Entity class storing rows of table Company
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param address Database column address 
   *  @param name Database column name  */
  case class CompanyRow(id: Int, address: String, name: String)
  /** GetResult implicit for fetching CompanyRow objects using plain SQL queries */
  implicit def GetResultCompanyRow(implicit e0: GR[Int], e1: GR[String]): GR[CompanyRow] = GR{
    prs => import prs._
    CompanyRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table company. Objects of this class serve as prototypes for rows in queries. */
  class Company(tag: Tag) extends Table[CompanyRow](tag, "company") {
    def * = (id, address, name) <> (CompanyRow.tupled, CompanyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, address.?, name.?).shaped.<>({r=>import r._; _1.map(_=> CompanyRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column address  */
    val address: Column[String] = column[String]("address")
    /** Database column name  */
    val name: Column[String] = column[String]("name")
  }
  /** Collection-like TableQuery object for table Company */
  lazy val Company = new TableQuery(tag => new Company(tag))
  
  /** Entity class storing rows of table CompanyClients
   *  @param companyId Database column company_id 
   *  @param clientId Database column client_id  */
  case class CompanyClientsRow(companyId: Int, clientId: Int)
  /** GetResult implicit for fetching CompanyClientsRow objects using plain SQL queries */
  implicit def GetResultCompanyClientsRow(implicit e0: GR[Int]): GR[CompanyClientsRow] = GR{
    prs => import prs._
    CompanyClientsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table company_clients. Objects of this class serve as prototypes for rows in queries. */
  class CompanyClients(tag: Tag) extends Table[CompanyClientsRow](tag, "company_clients") {
    def * = (companyId, clientId) <> (CompanyClientsRow.tupled, CompanyClientsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (companyId.?, clientId.?).shaped.<>({r=>import r._; _1.map(_=> CompanyClientsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column company_id  */
    val companyId: Column[Int] = column[Int]("company_id")
    /** Database column client_id  */
    val clientId: Column[Int] = column[Int]("client_id")
    
    /** Index over (clientId) (database name FK_7v0w3etaoa5fh24jmsc3qj344) */
    val index1 = index("FK_7v0w3etaoa5fh24jmsc3qj344", clientId)
    /** Index over (companyId) (database name FK_bh0g51kowjgvr5wvq81cfyij8) */
    val index2 = index("FK_bh0g51kowjgvr5wvq81cfyij8", companyId)
  }
  /** Collection-like TableQuery object for table CompanyClients */
  lazy val CompanyClients = new TableQuery(tag => new CompanyClients(tag))
  
  /** Entity class storing rows of table Employee
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param name Database column name 
   *  @param phone Database column phone 
   *  @param company Database column company  */
  case class EmployeeRow(id: Int, name: String, phone: String, company: Int)
  /** GetResult implicit for fetching EmployeeRow objects using plain SQL queries */
  implicit def GetResultEmployeeRow(implicit e0: GR[Int], e1: GR[String]): GR[EmployeeRow] = GR{
    prs => import prs._
    EmployeeRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table employee. Objects of this class serve as prototypes for rows in queries. */
  class Employee(tag: Tag) extends Table[EmployeeRow](tag, "employee") {
    def * = (id, name, phone, companyId) <> (EmployeeRow.tupled, EmployeeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, phone.?, companyId.?).shaped.<>({r=>import r._; _1.map(_=> EmployeeRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column phone  */
    val phone: Column[String] = column[String]("phone")
    /** Database column company  */
    val companyId: Column[Int] = column[Int]("company")
    
    /** Index over (company) (database name FK_3g3pshr815l6j5ala0okbnvrp) */
    val index1 = index("FK_3g3pshr815l6j5ala0okbnvrp", companyId)

    lazy val companyFk =
      foreignKey("employee_company_fk", companyId, Company)(_.id)
  }
  /** Collection-like TableQuery object for table Employee */
  lazy val Employee = new TableQuery(tag => new Employee(tag))

  // TODO^^ remove these tests!!!

  val x = for {
    (c,e) <- Company leftJoin Employee on (_.id === _.companyId)
  } yield (c,e)
  val s:Session = null
  val res = x.list(s)
  val res2 = x.first(s)

/*
  val x2 = (for {
    (c,e) <- Company leftJoin Employee on (_.id === _.companyId)
  } yield (c,e)) map { r => (r._1, Option(r._2)) } // no good, doesn't know how to pack
  val res2 = x2.list()(s)
*/

  val x3 = (for {
    (c,e) <- Company leftJoin Employee on (_.id === _.companyId)
  } yield (c,e))
  val res3 = x3.list(s) map { r => (r._1, Option(r._2)) }

  val x4 = (for {
    (c,e) <- Company leftJoin Employee on (_.id === _.companyId)
  } yield (c,e.?)) // Employee got def ? = (id.?, name.?, phone.?, companyId.?).shaped.<>({r=>import r._; _1.map(_=> EmployeeRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("ommited"))
  val res4 = x4.list(s)

  val x5 = (for {
      ((c,e),cc) <- (Company leftJoin Employee on (_.id === _.companyId)) leftJoin CompanyClients on { case ((c,e), cc) => cc.companyId === c.id }
    } yield (c,e, cc))

  val restricted = x5.filter{ case (company, employee, cclient) => company.name === "ACME" }


/*  val x6 = (for {
      (c,e) <- Company leftJoin Employee on (_.id === _.companyId)
      (c2, cc) <- c leftJoin CompanyClients on { case (c, cc) => cc.companyId === c.id }
    } yield (c2, e, cc))*/


}