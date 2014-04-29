package exec
package test

import org.scalatest.FunSuite

/**
 * Created by pedrofurla on 17/04/14.
 */
class Chronometer extends FunSuite {
  import Chronometer._

  import scalaz._
  import Scalaz._


  test("ElapsedTime[Id] is a monoid") {
    assertResult(elapsed(10) |+| elapsed(5))(elapsed(15))
    assertResult(elapsed(10) |+| Monoid[ElapsedTime[Chronon]].zero)(elapsed(10))
  }
  test("ElapsedTime[NEL] is a semigroup") {
    assertResult(elapseds(1,2) |+| elapseds(3,4))(elapseds(1,2,3,4))
  }

  val Et = ElapsedTime

  val etSample: ElapsedTime[Long] = ElapsedTime(10L)
  val etSampleNel: ElapsedTime[NEL[Long]] = ElapsedTime(NonEmptyList.nels(1L,2L,3L))
  val sampleNelEt: NEL[ElapsedTime[Long]] = etSampleNel.time map { ElapsedTime(_) }
  val sampleNelEtNel: NEL[ElapsedTime[NEL[Long]]] = etSampleNel.time map {x => Applicative[ET_CC[NEL]#λ].point(x) }

  test("ElapsedTime type classes") {

    implicitly[Monoid[ElapsedTime[Int]]]
    implicitly[Semigroup[ElapsedTime[NEL[Int]]]]

    implicitly[Functor[ET_CC[NEL]#λ]]
    implicitly[Applicative[ET_CC[NEL]#λ]]
    implicitly[Apply[ET_CC[NEL]#λ]]
    implicitly[Foldable[ET_CC[NEL]#λ]]
    implicitly[Foldable1[ET_CC[NEL]#λ]]

    assert(etSampleNel.map{_ + 1 } == ElapsedTime(NonEmptyList.nels(2L,3L,4L)))

    assert(etSampleNel.foldMap1(identity) == etSampleNel.foldMap(identity))
    assert(etSampleNel.foldMap1(identity) == 6L)
    assert(etSampleNel.foldMap1(ElapsedTime.apply) == ElapsedTime(6L))

    assert(sampleNelEt.foldMap1{ x => x.nel } == etSampleNel)
    assert(sampleNelEtNel.foldMap1{ identity } == etSampleNel)

    assert(sampleNelEt.foldRight(ElapsedTime(0L))(_ |+| _) == ElapsedTime(6L))
    assert(sampleNelEt.foldRight1(_ |+| _) == ElapsedTime(6L))

  }

  val Eto = ElapsedTimeOf

  val etoSampleIdId = ElapsedTimeOf(1L, etSample )
  val etoSampleIdNel = ElapsedTimeOf(1L, etSampleNel )
  val etoSampleNelNel = ElapsedTimeOf(Nel(1,2,3), etSampleNel )
  val etoSampleNelId = ElapsedTimeOf(Nel(1,2,3), etSample )

  val etoSampleNelNel1 = ElapsedTimeOf(Nel(2,3,4), etSampleNel )
  val etoSampleNelId1 = ElapsedTimeOf(Nel(2,3,4), etSample )

  val etoSampleNelF = ElapsedTimeOf(Nel((x:Int) => x+1), etSample )

  test("ElapsedTimeOf type classes") {
    implicitly[Monoid[ElapsedTimeOf[Int,Int]]]
    implicitly[Semigroup[ElapsedTimeOf[Int,NEL[Int]]]]
    implicitly[Semigroup[ElapsedTimeOf[NEL[Int],Int]]]
    implicitly[Semigroup[ElapsedTimeOf[NEL[Int],NEL[Int]]]]

    implicitly[Functor[ETO_NEL[Int]#λ]]
    implicitly[Applicative[ETO_NEL[Int]#λ]]
    implicitly[Apply[ETO_NEL[Int]#λ]]
    implicitly[Foldable[ETO_NEL[Int]#λ]]
    implicitly[Foldable1[ETO_NEL_NEL[Int]#λ]]

    assert(etoSampleNelNel.map{ _ + 1 } == etoSampleNelNel1)
    assert(etoSampleNelId.map{ _ +  1 } == etoSampleNelId1)

    assert(etoSampleNelId.foldMap1(identity) == 6)
  }

  test("ElapsedTimeOf[X, Id] is a monoid where X != NEL") {
    val left: ElapsedTimeOf[Int, Chronon] = elapsedOf(1, elapsed(5L))

    assertResult(
      left |+| elapsedOf(2, elapsed(3))
    )(
      elapsedOf(3, elapsed(8))
    )
    assertResult(
      left |+| Monoid[ElapsedTimeOf[Int,Chronon]].zero
    )(left)
  }
  test("ElapsedTimeOf[NEL, Id] is a semigroup on A and a monoid on CC") {
    val left0 = elapsedOf(Nel(1), Monoid[ElapsedTime[Chronon]].zero)
    val left1 = elapsedOf(Nel(1), elapsed(5))
    assertResult(
      left1 |+| elapsedOf(Nel(2), elapsed(3))
    )(
      elapsedOf(Nel(1,2), elapsed(8))
    )
    assertResult(
      left0 |+| left1
    )(
      elapsedOf(Nel(1,1), elapsed(5))
    )
  }

  test("ElapsedTimeOf[X, NEL] is a semigroup if monoid on A and a semigroup on CC") {
    val left0 = elapsedOf(Monoid[Int].zero, elapseds(5))
    val left1 = elapsedOf(1, elapseds(10,11))
    assertResult(
      left1 |+| elapsedOf(2, elapseds(15))
    )(
      elapsedOf(3, elapseds(10,11,15))
    )
    assertResult(
      left0 |+| left1
    )(
      elapsedOf(1, elapseds(5,10,11))
    )
  }
  test("ElapsedTimeOf[NEL, NEL] is a semigroup") {
    assertResult(
      elapsedOf(Nel(1), elapseds(15,5)) |+| elapsedOf(Nel(2), elapseds(3))
    )(
      elapsedOf(Nel(1,2), elapseds(15,5,3))
    )
    assertResult(
      elapsedOf(Nel(1), elapseds(15,5)) |+| elapsedOf(Nel(2), elapseds(3))
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
    assertResult(elapseds(1,2,3).map{(x:Chronon) => x + 1 })(elapseds(2,3,4))
  }

}
