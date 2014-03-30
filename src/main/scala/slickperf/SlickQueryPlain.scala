package slickperf

import scala.slick.jdbc.{ StaticQuery => Q }
import Q.interpolation
import client.test.TestHelper._

object SlickQueryPlain  {

  import MySqlConnection._

  def main(args: Array[String]) {
    execute(10) {
      test()
    }
  }

  def test() {
    val duration = chronometer {
      connect.withTransaction {
        implicit session =>
          sql"select u.*,acc.* from MAIN_TC_USER u, PAY_TD_ACCOUNT acc where acc.user_id = u.id and u.id = 1".as[(MainTcUserRow, PayTdAccountRow)].list
      }
    }
    printTime(duration,1, MS)
  }
}