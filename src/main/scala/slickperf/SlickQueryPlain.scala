package slickperf

import slickperf.Tables._
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation
import client.test.TestHelper._

object SlickQueryPlain  {

  def main(args: Array[String]) {
    execute(10) {
      test()
    }
  }

  def test() {
    val duration = chronometer {
      MySqlConnection.connect.withTransaction {
        implicit session =>
          sql"select u.*,acc.* from MAIN_TC_USER u, PAY_TD_ACCOUNT acc where acc.user_id = u.id and u.id = 1".as[(MainTcUserRow, PayTdAccountRow)].list
      }
    }
    printTime(duration, SEC)
  }
}