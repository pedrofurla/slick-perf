package slickperf

import support.{TablesDefinition, SlickConnection, SlickProfile}

//import scala.slick.model.ForeignKeyAction
//import scala.slick.lifted
//import scala.slick.lifted.MappedProjection

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables2 extends TablesDefinition { self: SlickConnection with SlickProfile =>

  import profile.simple._

  lazy val ddl: profile.DDL = Clients.ddl ++ Companies.ddl ++ CompanyClients.ddl ++ Employees.ddl

  // *** THE MODEL
  case class CompanyRow(id: Int, address: String, name: String)
  case class CompanyClientsRow(companyId: Int, clientId: Int)
  case class EmployeeRow(id: Int, name: String, phone: String, company: Int)
  case class ClientRow(id: Int, address: Option[String], name: String)

  // *** THE MAPPINGS
  class Clients(tag: Tag) extends Table[ClientRow](tag, "client") {
    def * = (id, address, name) <> (ClientRow.tupled, ClientRow.unapply)
    def ? = (id.?, address, name.?).shaped.<>(
      { r:(Option[Int],Option[String],Option[String]) =>
        import r._
        _1.map(_ => ClientRow.tupled((_1.get,_2,_3.get)))
      },
      (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val address: Column[Option[String]] = column[Option[String]]("address")
    val name: Column[String] = column[String]("name")
  }

  lazy val Clients = new TableQuery(tag => new Clients(tag))

  class Companies(tag: Tag) extends Table[CompanyRow](tag, "company") {
    def * = (id, address, name) <> (CompanyRow.tupled, CompanyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, address.?, name.?).shaped.<>(
      { r:(Option[Int],Option[String],Option[String]) =>
        import r._
        _1.map(_=> CompanyRow.tupled((_1.get, _2.get, _3.get)))
      }, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val address: Column[String] = column[String]("address")
    val name: Column[String] = column[String]("name")
  }

  lazy val Companies = new TableQuery(tag => new Companies(tag))

  class CompanyClients(tag: Tag) extends Table[CompanyClientsRow](tag, "company_clients") {
    def * = (companyId, clientId) <> (CompanyClientsRow.tupled, CompanyClientsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (companyId.?, clientId.?).shaped.<>(
      { r:(Option[Int],Option[Int]) =>
        import r._; 
        _1.map(_=> CompanyClientsRow.tupled((_1.get, _2.get)))
      }, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val companyId: Column[Int] = column[Int]("company_id")
    val clientId: Column[Int] = column[Int]("client_id")

    val pk = primaryKey(this.tableName+"_pk", (companyId,clientId))

    val index1 = index(this.tableName+"_idx_clientId", clientId)
    val index2 = index(this.tableName+"_idx_companyId", companyId)
    lazy val companyFk = foreignKey(this.tableName+"_company_fk", companyId, Companies)(_.id)
    lazy val clientFk = foreignKey(this.tableName+"_client_fk", clientId, Clients)(_.id)
  }

  lazy val CompanyClients = new TableQuery(tag => new CompanyClients(tag))

  class Employees(tag: Tag) extends Table[EmployeeRow](tag, "employee") {
    def * = (id, name, phone, companyId) <> (EmployeeRow.tupled, EmployeeRow.unapply)

    def ? = (id.?, name.?, phone.?, companyId.?).shaped.<>(
      { r:(Option[Int],Option[String],Option[String],Option[Int]) =>
        import r._ 
        _1.map(_=> EmployeeRow.tupled((_1.get, _2.get, _3.get, _4.get)))
      }, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Column[String] = column[String]("name")
    val phone: Column[String] = column[String]("phone")
    val companyId: Column[Int] = column[Int]("company_id")
    
    val index1 = index(this.tableName+"_idx_companyId", companyId)
    lazy val companyFk = foreignKey(this.tableName+"_company_fk", companyId, Companies)(_.id)
  }
  
  lazy val Employees = new TableQuery(tag => new Employees(tag))

  import scala.slick.jdbc.{GetResult => GR}

  // **** JDBC RESULT 'EXTRACTORS' *****
  implicit def GetResultEmployeeRow(implicit e0: GR[Int], e1: GR[String]): GR[EmployeeRow] = GR{
    prs => import prs._
    EmployeeRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  implicit def GetResultCompanyClientsRow(implicit e0: GR[Int]): GR[CompanyClientsRow] = GR{
    prs => import prs._
    CompanyClientsRow.tupled((<<[Int], <<[Int]))
  }
  implicit def GetResultCompanyRow(implicit e0: GR[Int], e1: GR[String]): GR[CompanyRow] = GR{
    prs => import prs._
    CompanyRow.tupled((<<[Int], <<[String], <<[String]))
  }
  implicit def GetResultClientRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[String]): GR[ClientRow] = GR{
    prs => import prs._
    ClientRow.tupled((<<[Int], <<?[String], <<[String]))
  }
  
  // TODO^^ remove these tests!!!

  /*
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

    */
}