package slickperf

import client.test.TestHelper._

object SlickQuery  {

  import MySqlConnection._
  import simple._

  def findUser(id: Column[Int]) = for {
    account <- PayTdAccount
    user <- account.mainTcUserFk if user.id === id
  } yield (user, account)

  val findUserCompiled = Compiled(findUser _)

  def test() = {
    val duration = chronometer {
      MySqlConnection.connect.withTransaction { implicit session =>
        findUserCompiled(1).run
      }
    }

    printTime(duration, 1, MS)

  }

  def main(args: Array[String]) =
    execute(10) {
      test()
    }

}