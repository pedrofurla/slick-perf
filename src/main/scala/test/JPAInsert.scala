package client.test

import javax.persistence.Persistence

import TestHelper._
import jpaperf.{MainUser, PayAccount}

object JPAInsert {

  def main(args: Array[String]) {    
    for (i <- numberOfInserts) {
      add(i)
    }
  }

  private def add(number: Int) {
    val factory = Persistence.createEntityManagerFactory("slickperf-persistence")
    val em = factory.createEntityManager()
    val duration = chronometer {
      em.getTransaction().begin();
      for (i <- 1 to number) {
        val user = new MainUser
        user.username = "zlaja"
        user.password = "zlaja"
        user.name = "zlaja"
        user.surname = "zlaja"
        em persist user

        val account = new PayAccount
        account.id = i
        account.reserved = 200
        account.amount = 200
        account.setUserId(user)
        user.getPayAccountList.add(account)
        em persist account
      }
      em.getTransaction().commit();
    }
    printTime(duration,number,MS)
  }

}