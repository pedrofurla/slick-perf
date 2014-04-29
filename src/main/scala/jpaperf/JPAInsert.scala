package jpaperf

import javax.persistence.EntityManager
import exec.TestHelper._
import exec.DbRun
import exec.JPA._

class JPAInsert(jpa:exec.JPA) extends DbRun {
  import jpa._

  val title = s"JPA ${jpa.persistenceUnit} Inserting users and one account per user" // + " with one connection"

  val action2: EntityManager => Unit = (em:EntityManager) => {
    val user = newUser
    em persist user

    val account = newAccount(user)
    em persist account
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

  import exec.Chronograph2._
  import scalaz._
  import Scalaz._

  def run2(repetitions:NonEmptyList[Int]):ElapsedTimeOf[String, NonEmptyList[Chronon]] = {

    println(title)

    val res: NonEmptyList[ElapsedTimeOf[NonEmptyList[Int], NonEmptyList[Chronon]]] = for (i <- repetitions) yield {
      printMe(performWithTransactionN(i)(action(_))).nelnel
    }
    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)
  }

}