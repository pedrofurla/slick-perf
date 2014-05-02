package jpaperf

import javax.persistence._
import scala.beans.BeanProperty
import support.JpaConnection

/**
 * Created by pedrofurla on 08/04/14.
 *
 * Mappings and hibernate entities based on the blog post http://blog.jbaysolutions.com/2012/12/17/jpa-2-relationships-many-to-many/
 *
 */
object Entities {

  object jpa2 extends {
    val persistenceUnit="jpa2-mysql"
  } with JpaConnection

  //val jpa2 = newEntityManager("jpa2-mysql")

  def gen(create:Boolean=false):Unit = {
    import org.hibernate.cfg._
    val cfg = new Configuration().
      addAnnotatedClass(classOf[Company]).
      addAnnotatedClass(classOf[Client]).
      addAnnotatedClass(classOf[Employee]).
      setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL5Dialect").
      setProperty(AvailableSettings.USER, "root").
      setProperty(AvailableSettings.URL, "jdbc:mysql://localhost:3306/jpa2")
    import org.hibernate.tool.hbm2ddl.SchemaExport
    val exporter = new SchemaExport(cfg);
    exporter.setDelimiter(";")
    exporter.setOutputFile("schema.sql");
    //exporter.create(true,false)
    exporter.execute(true,create,false,false)
  }


  @Entity(name = "Client")
  @Table(name = "client")
  class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    @BeanProperty
    var id: Int = _

    @Basic(optional = false)
    @Column(name = "name")
    @BeanProperty
    var name:String = _

    @Column(name = "address")
    @BeanProperty
    var address:String = _

    import java.util.Set
    import java.util.Collections
    import java.util.List

    @ManyToMany(mappedBy = "clients")
    var companyCollection:List[Company] = Collections.emptyList() // NOTE: Set makes a lot more sense here, but list is more convenient

    def toTuple = (id,name,address,companyCollection)

    override def toString = this.getClass.getSimpleName+":"+toTuple

    override def hashCode() = toTuple.hashCode()

    override def equals(obj: scala.Any) = toTuple.equals(obj.asInstanceOf[Client].toTuple)
  }

  @Entity(name = "Company")
  @Table(name = "company")
  class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    @BeanProperty
    var id: Int = _

    @Basic(optional = false)
    @Column(name = "name")
    @BeanProperty
    var name: String = _

    @Basic(optional = false)
    @Column(name = "address")
    @BeanProperty
    var address: String = _


    import java.util.Set
    import java.util.Collections
    import java.util.List

    @JoinTable(
      name = "company_clients",
      joinColumns = Array(new JoinColumn(name = "company_id", referencedColumnName = "id")),
      inverseJoinColumns = Array(new JoinColumn(name = "client_id", referencedColumnName = "id"))
    )
    @ManyToMany
    @BeanProperty
    var clients: List[Client] = Collections.emptyList()  // NOTE: Set makes a lot more sense here, but list is more convenient

    @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "company")
    @BeanProperty
    var employees: List[Employee] = Collections.emptyList() // NOTE: Set makes a lot more sense here, but list is more convenient

    def toTuple = (id,name,address,clients, "employees: "+employees.size())
    // NOTE: see note at Company.toTuple, same here with employees

    override def toString = this.getClass.getSimpleName+":"+toTuple

    override def hashCode() = toTuple.hashCode()

    override def equals(obj: scala.Any) = toTuple.equals(obj.asInstanceOf[Company].toTuple)

  }


  @Entity(name = "Employee")
  @Table(name = "employee")
 /* @NamedQueries(Array(
    new NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e"),
    new NamedQuery(name = "Employee.findById", query = "SELECT e FROM Employee e WHERE e.id = :id"),
    new NamedQuery(name = "Employee.findByName", query = "SELECT e FROM Employee e WHERE e.name = :name"),
    new NamedQuery(name = "Employee.findByPhone", query = "SELECT e FROM Employee e WHERE e.phone = :phone")))*/
   class Employee  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    @BeanProperty
    var id: Int = _;

    @Basic(optional = false)
    @Column(name = "name")
    @BeanProperty
    var name: String = _;

    @Basic(optional = false)
    @Column(name = "phone")
    @BeanProperty
    var phone: String = _;

    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @BeanProperty
    var company: Company = _;

    def toTuple = (id,name,phone,"Company: "+(company.id,company.name))
    // ^ NOTE: can't use just company, otherwise there is a infinity cycle, since company points to the same employee

    override def toString = this.getClass.getSimpleName+":"+toTuple

    override def hashCode() = toTuple.hashCode()

    override def equals(obj: scala.Any) = toTuple.equals(obj.asInstanceOf[Employee].toTuple)
  }
}

