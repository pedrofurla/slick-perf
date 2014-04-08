package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec.Chronograph
import exec.Reports._
import exec.JPA._

object JPAInsert {

  def run:Report = {
    printMe(Report("Inserting users and one account per user with one connection",
    for (i <- numberOfInserts) yield {
      printMe(reportLine(performWithTransactionN(i)(action(_))))
      //println(performInTransactionN(i)(action(_)))
    },
    Chronograph.Micros))
  }

  private final def action(em:EntityManager):Unit = {
    val user = newUser
    em persist user

    val account = newAccount(user)
    em persist account
  }

  private final def newUser:MainUser = {
    val user = new MainUser
    user.username = "zlaja"
    user.password = "zlaja"
    user.name = "zlaja"
    user.surname = "zlaja"
    user
  }

  private final def newAccount(user:MainUser) = {
    val account = new PayAccount
    account.reserved = 200
    account.amount = 200
    account.setUserId(user)
    user.getPayAccountList.add(account)
    account
  }


}