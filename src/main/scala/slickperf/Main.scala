package slickperf

/**
 * Created by pedrofurla on 28/03/14.
 */
object Main {
  def main(args: Array[String]) {
    import scala.util.control.Exception._

    MySqlConnection.createSchema
    val res = allCatch either {
      println("Inserting")
      SlickInsert.main(args)
      println("Querying:")
      SlickQuery.main(args)
      println("Querying plain:")
      SlickQueryPlain.main(args)
    }
    MySqlConnection.destroySchema
    res.fold(x => x.printStackTrace(), _ => {})
  }
}
