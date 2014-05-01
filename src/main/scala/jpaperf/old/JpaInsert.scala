package jpaperf.old

import javax.persistence.EntityManager
import exec.TestHelper._
import exec._
import support.JpaConnection

class JpaInsert(jpa:JpaConnection) extends DbRun {
  import jpa._
  import EntitiesOld._

  val title = s"JPA ${jpa.persistenceUnit} Inserting users and one account per user" // + " with one connection"

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
    //user.getPayAccountList.add(account)
    account
  }

  import exec.Chronometer._
  import scalaz._
  import Scalaz._

  def run(repetitions:NEL[Int]):ElapsedTimeOf[String, NEL[Chronon]] = {
    println(title)

    val res: NEL[ElapsedTimeOf[NEL[Int], NEL[Chronon]]] = for (i <- repetitions) yield {
      printMe(performWithTransactionN(i)(action(_))).nelnel
    }
    val tmp = res.foldMap1(identity)
    tmp.copy(value = title)
  }

}