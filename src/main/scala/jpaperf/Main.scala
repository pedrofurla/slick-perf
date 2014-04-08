package jpaperf

import slickperf.MySqlConnection
import exec.JPA

/**
 * Created by pedrofurla on 29/03/14.
 */
object Main {
  def main(args: Array[String]) {
    MySqlConnection.inSchema(run())
  }

  def run():Unit = {
    JPAInsert.run
    JPAQuery.run
  }
}
