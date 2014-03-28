package slickperf

import scala.slick.driver.MySQLDriver.simple._
import client.test.TestHelper._

object SlickQuery  {

  def findUser(id: Column[Int]) = for {
    account <- Tables.PayTdAccount
    user <- account.mainTcUserFk if user.id === id
  } yield (user, account)

  val findUserCompiled = Compiled(findUser _)

  def test() = {
    val duration = chronometer {
      MySqlConnection.connect.withTransaction { implicit session =>
        findUserCompiled(1).run
      }
    }

    printTime(duration, SEC)

  }

  def main(args: Array[String]) =
    execute(10) {
      test()
    }

}