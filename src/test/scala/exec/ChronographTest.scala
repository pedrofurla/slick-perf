package exec

import org.scalatest.FunSuite

/**
 * Created by pedrofurla on 17/04/14.
 */
object ChronographTest extends FunSuite {
  import Chronograph2._

  import scalaz._
  import Scalaz._
/*

  test("TimeUnit.as") {
    assertResult(Nanos.as(Nanos.chrononsInSecond, Seconds))(1L)
    assertResult(Nanos.as(Nanos.chrononsInSecond, Micros))(1e6.toLong)
  }

  test("ElapsedTime[Id] is a monoid") {
    assertResult(elapsed(10) |+| elapsed(5))(elapsed(15))
    assertResult(elapsed(10) |+| Monoid[ElapsedTime[Id]].zero)(elapsed(10))
  }
  test("ElapsedTime[NEL] is a semigroup") {
    assertResult(elapseds(1,2) |+| elapseds(3,4))(elapseds(1,2,3,4))
  }

  test("Should compile") {

    implicitly[Semigroup[ElapsedTime[NEL]]]
    implicitly[Monoid[ElapsedTime[Id]]]

    implicitly[Semigroup[ElapsedTimeOf[Int,NEL]]]
    implicitly[Semigroup[ElapsedTimeOf[NEL[Int],NEL]]]
    implicitly[Monoid[ElapsedTimeOf[Int,Id]]]
    implicitly[Semigroup[ElapsedTimeOf[NEL[Int],Id]]]

    val ETO = ElapsedTimeOf

    implicitly[Functor[ETO.ETO_NEL[NEL]#λ]]
    implicitly[Functor[ETO.ETO_NEL[Id]#λ]]

//    ETO(NEL.nels(1,2,3),elapseds(1,2,3)).map(_ + 1)
    ETO(NEL.nels(1,2,3),elapsed(1)).map(_ + 1)

    implicitly[Foldable1[ETO.ETO_NEL[NEL]#λ]]
    implicitly[Foldable1[ETO.ETO_NEL[Id]#λ]]

  }

  test("ElapsedTimeOf[X, Id] is a monoid where X != NEL") {
    //import ElapsedTime._
    val left: ElapsedTimeOf[Int, Scalaz.Id] = elapsedOf(1, elapsed(5))

    //val m = Monoid[ElapsedTimeOf[Int,Id]]
    //implicit val met = Monoid[Id[Int]]
    //import m.monoidSyntax._ // Scala can't get the syntax out of ElapsedTimeOf in this case
    //import ElapsedTimeOf._ // didn't work
    //Fixed by making ETO semigroup specific to NEL


    assertResult(
      left |+| elapsedOf(2, elapsed(3))
    )(
      elapsedOf(3, elapsed(8))
    )
    assertResult(
      left |+| Monoid[ElapsedTimeOf[Int,Id]].zero
    )(left)
  }
  test("ElapsedTimeOf[NEL, Id] is a semigroup on A and a monoid on CC") {
    val left0 = elapsedOf(NEL(1), Monoid[ElapsedTime[Id]].zero)
    val left1 = elapsedOf(NEL(1), elapsed(5))
    assertResult(
      left1 |+| elapsedOf(NEL(2), elapsed(3))
    )(
      elapsedOf(NEL(1,2), elapsed(8))
    )
    assertResult(
      left0 |+| left1
    )(
      elapsedOf(NEL(1,1), elapsed(5))
    )
  }

  test("ElapsedTimeOf[X, NEL] is a semigroup if monoid on A and a semigroup on CC") {
    val left1 = elapsedOf(1, elapseds(10,11))
    val left0 = elapsedOf(Monoid[Int].zero, elapseds(5))
    assertResult(
      left1 |+| elapsedOf(2, elapseds(15))
    )(
      elapsedOf(3, elapseds(10,11,5))
    )
    assertResult(
      left0 |+| left1
    )(
      elapsedOf(1, elapseds(10,11,5))
    )
  }
  test("ElapsedTimeOf[NEL, NEL] is a semigroup") {
    assertResult(
      elapsedOf(NEL(1), elapseds(15,5)) |+| elapsedOf(NEL(2), elapseds(3))
    )(
      elapsedOf(NEL(1,2), elapseds(15,5,3))
    )
    assertResult(
      elapsedOf(NEL(1), elapseds(15,5)) |+| elapsedOf(NEL(2), elapseds(3))
    )(
      ofElapsed(elapseds(15,5,3),1,2)
    )
  }

  test("ElapsedTime[NEL] has map but it not a functor") {
    //import ElapsedTime._
    //NonEmptyList(1,2,3).map(_ + 1)
    //ElapsedTime.ElapsedTimeList.map(elapseds(1L,2L,3L)){(x:Long) => x + 1 }
   // import ElapsedTimeList.functorSyntax._
    //import scalaz.syntax.functor._
    assertResult(elapseds(1L,2L,3L).map{(x:Chronon) => x + 1 })(elapseds(2,3,4))
  }
*/

}
