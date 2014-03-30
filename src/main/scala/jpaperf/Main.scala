package jpaperf

import client.test.JPAInsert
import slickperf.MySqlConnection

/**
 * Created by pedrofurla on 29/03/14.
 */
object Main {
  def main(args: Array[String]) {
    MySqlConnection.createSchema
    JPAInsert.main(args)
    MySqlConnection.destroySchema
  }
}
