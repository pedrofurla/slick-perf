trait EntityBase {
  def id: String
}

trait Repository {
  type Entity <: EntityBase

  def get(id: String): Option[Entity]
}

trait SlickRepository extends Repository {
  val profile: scala.slick.driver.JdbcProfile
  val simple = profile.simple
  def database: profile.simple.Database

  import simple._

  abstract class BaseTable(tag: Tag, tableName: String) extends Table[Entity](tag, tableName) {
    def id: Column[String]
  }

  val table: TableQuery[_ <: BaseTable]  // needs to be covariant

  override def get(id: String): Option[Entity] = database.withSession { implicit session =>
    table.filter(_.id === id).list.headOption
  }

}

case class User(id: String, name: String, email: String) extends EntityBase

abstract class UserRepository extends SlickRepository {

  type Entity = User

  import simple._

  class UserTable(tag: Tag) extends BaseTable(tag, "user") {
    def id = column[String]("userid", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")

    def * = (id, name, email) <> (User.tupled, User.unapply)
  }

  val table = TableQuery[UserTable]
}