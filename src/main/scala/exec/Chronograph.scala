package exec

import scalaz.syntax.MonoidSyntax

/**
 *
 * Tools to measure time of execution.
 *
 * Created by pedrofurla on 31/03/14.
 */
object Chronograph {

  /** Chronon is the quantum of time, here in our silly computers it is a Long, I guess Double could be equally valid
      but dealing with floating points wouldn't be very useful for the project use case */
  type Chronon = Long

  /**
   *
   * @param chrononsInSecond how many time units are there in a second?
   * @param name name of the time unit
   */
  sealed case class TimeUnit(chrononsInSecond: Chronon, name:String) 

  private final val nanosInSeconds = 1e9.toLong

  object TimeUnit {
    /** Knows how to obtain "now" in a TimeUnit */
    implicit class TimeUnitOps(val tu:TimeUnit) extends AnyVal {
      def getTime2 = System.nanoTime()/(nanosInSeconds/tu.chrononsInSecond)
    }

  }

  val Nanos  = new TimeUnit(nanosInSeconds, "ns")
  val Micros = new TimeUnit(1e6.toLong, "μs")
  val Millis = new TimeUnit(1e3.toLong, "ms")

  import scalaz._
  import Scalaz._

  /** Elapsed time for a given time unit */
  case class ElapsedTime(time: Chronon, unit:TimeUnit)

  /** The result of measuring how long it takes to obtain a `B` */
  //case class ElapsedTimeOf[B](value:B, elapsed: Chronon, unit:TimeUnit)
  case class ElapsedTimeOf[A](value:A, elapsed: ElapsedTime)


  /** Times the execution of `u` */
  def chronometer[B](u: => B): TimeUnit => ElapsedTimeOf[B] = (timer: TimeUnit) => {
    val start = timer.getTime2
    val res = u
    val end = timer.getTime2
    ElapsedTimeOf(res, ElapsedTime(end - start, timer))
  }

  /** Times the execution of `f` */
  def chronograph[A,B](timer: TimeUnit)(f: (A => B)): A => ElapsedTimeOf[B] = a => chronometer(f(a))(timer)

  def nanos[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Nanos)(f)
  def micros[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Micros)(f)
  def millis[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Millis)(f)
}


object Chronograph2 {

  /** Chronon is the quantum of time, here in our silly computers it is a Long, I guess Double could be equally valid
      but dealing with floating points wouldn't be very useful for the project use case */
  type Chronon = Long

  /**
   *
   * @param chrononsInSecond how many time units are there in a second?
   * @param name name of the time unit
   */
  case class TimeUnit(chrononsInSecond: Chronon, name:String)

  final val nanosInSeconds = 1e9.toLong

  object TimeUnit {
    /** Knows how to obtain "now" in a TimeUnit */
    implicit class TimeUnitOps(val tu:TimeUnit) extends AnyVal {
      def getTime = System.nanoTime()/(nanosInSeconds/tu.chrononsInSecond)
      /** Only safe for narrowing the precision, widening will lose information. */
      def as(v:Chronon, other:TimeUnit) = (v/(tu.chrononsInSecond/other.chrononsInSecond.toDouble)).toLong
    }
  }

  val Nanos   = new TimeUnit(nanosInSeconds, "ns")
  val Micros  = new TimeUnit(1e6.toLong, "μs")
  val Millis  = new TimeUnit(1e3.toLong, "ms")
  val Seconds = new TimeUnit(1L, "s")

  import scalaz._
  import Scalaz._

  type NEL[A] = NonEmptyList[A]
  val Nel = NonEmptyList

  type ET_CC[CC[_]] = {
    type λ[A]=ElapsedTime[CC[A]]
  }

  type ETO_NEL_NEL[B] = { type λ[A]=ElapsedTimeOf[NEL[A], NEL[B]] }
  type ETO_NEL[B] = { type λ[A]=ElapsedTimeOf[NEL[A], B] }
  type ETO_BI[AA[_],BB[_]] = { type λ[A,B]=ElapsedTimeOf[AA[A], BB[B]] }

  /** Elapsed time for a given time unit in nano seconds*/
  case class ElapsedTime[A](time: A) {
    def nel = ElapsedTime(Nel(time))
  }
  object ElapsedTime {
    implicit def canBeMonoid[A](implicit m:Monoid[A]):Monoid[ElapsedTime[A]] = {
      Monoid.instance[ElapsedTime[A]](
        (e1,e2) => e1.copy(time = e1.time |+| e2.time),
        ElapsedTime[A](m.zero)
      )
    }
    implicit def canBeSemigroup[A](implicit m:Semigroup[NEL[A]]):Semigroup[ElapsedTime[NEL[A]]] = {
      Semigroup.instance[ElapsedTime[NEL[A]]](
        (e1,e2) => e1.copy(time = e1.time |+| e2.time)
      )
    }
    implicit def EtPimps[CC[_]](implicit evf: Applicative[CC], etf: Foldable1[CC]) =
      new Applicative[ET_CC[CC]#λ] with Foldable1[ET_CC[CC]#λ] {
      def point[A](a: => A): ElapsedTime[CC[A]]= ElapsedTime[CC[A]](evf.point(a))
      def ap[A,B](fa: => ElapsedTime[CC[A]])(f: => ElapsedTime[CC[A => B]]): ElapsedTime[CC[B]] =
        ElapsedTime(evf.ap(fa.time)(f.time))


      def foldMap1[A,B](fa: ElapsedTime[CC[A]])(f: A => B)(implicit F: Semigroup[B]): B = fa.time.foldMap1(f)
      def foldRight1[A](fa: ElapsedTime[CC[A]])(f: (A, => A) => A): A = fa.time.foldRight1(f)
      def foldRight[A, B](fa: ElapsedTime[CC[A]], z: => B)(f: (A, => B) => B): B = fa.time.foldRight(z)(f)
    }
    implicit def unapplyMALocal[TC[_[_]],CC[_], A0](implicit TC0: TC[ET_CC[CC]#λ]) = Unapply.unapplyMA[TC, ET_CC[CC]#λ, A0]
  }

  /** The result of measuring how long it takes to obtain a `A` */
  case class ElapsedTimeOf[A,B](value:A, elapsed: ElapsedTime[B]) {
    def nel = this.copy(value = Nel(value))
    def nelnel = this.copy(value = Nel(value), elapsed = elapsed.nel)
  }

  object ElapsedTimeOf {
    implicit def isShow[A,B]:Show[ElapsedTimeOf[A,B]] = Show.show { _.toString }
    implicit def isMonoid[A,B](implicit meto:Monoid[A], met:Monoid[ElapsedTime[B]]):Monoid[ElapsedTimeOf[A,B]] = {
      Monoid.instance[ElapsedTimeOf[A,B]](
        (e1,e2) => e1.copy(value = e1.value |+| e2.value, elapsed = e1.elapsed |+| e2.elapsed),
        ElapsedTimeOf(meto.zero, met.zero)
      )
    }
    implicit def isSemigroup[A,B](
         implicit seto: Semigroup[A], set:Semigroup[ElapsedTime[B]]):Semigroup[ElapsedTimeOf[A,B]] = {
      Semigroup.instance[ElapsedTimeOf[A,B]](
        (e1,e2) => e1.copy(value = e1.value |+| e2.value,elapsed = e1.elapsed |+| e2.elapsed)
      )
    }
    implicit def EtoPimps[ETB](implicit met:Monoid[ElapsedTime[ETB]]) = new Applicative[ETO_NEL[ETB]#λ] with Foldable1[ETO_NEL[ETB]#λ] {
      type F[A] = ElapsedTimeOf[NEL[A],ETB]
      def point[A](a: => A): F[A] = ElapsedTimeOf(Nel(a), met.zero)
      def ap[A,B](fa: => F[A])(f: => F[A => B]): F[B] = fa.copy(fa.value <*> f.value) // TODO^^ f.time is lost, this is wrong!
      // override def map[A, B](fa: F[A])(f: A => B): F[B] = ap(fa)(point(f)) // Applicative.map

      def foldMap1[A,B](fa: F[A])(f: A => B)(implicit F: Semigroup[B]): B = fa.value.foldMap1(f)
      def foldRight1[A](fa: F[A])(f: (A, => A) => A): A = fa.value.foldRight1(f)
      def foldRight[A, B](fa: F[A], z: => B)(f: (A, => B) => B): B = fa.value.foldRight(z)(f)
    }
    implicit def EtoPimps2[ETB](implicit met:Semigroup[ElapsedTime[NEL[ETB]]], metb:Monoid[ETB]) = new Applicative[ETO_NEL_NEL[ETB]#λ] with Foldable1[ETO_NEL_NEL[ETB]#λ] {
      type F[A] = ElapsedTimeOf[NEL[A],NEL[ETB]]
      def point[A](a: => A): F[A] = ElapsedTimeOf(Nel.nels(a), Applicative[ET_CC[NEL]#λ].point(metb.zero)) // TODO metb.zero? This is questionable
      def ap[A,B](fa: => F[A])(f: => F[A => B]): F[B] = fa.copy(fa.value <*> f.value) // TODO^^ f.time is lost, this is wrong!
      // override def map[A, B](fa: F[A])(f: A => B): F[B] = ap(fa)(point(f)) // Applicative.map

      def foldMap1[A,B](fa: F[A])(f: A => B)(implicit F: Semigroup[B]): B = fa.value.foldMap1(f)
      def foldRight1[A](fa: F[A])(f: (A, => A) => A): A = fa.value.foldRight1(f)
      def foldRight[A, B](fa: F[A], z: => B)(f: (A, => B) => B): B = fa.value.foldRight(z)(f)
    }
    implicit def EtoPimps3[AA[_],BB[_]](implicit fa:Functor[AA], fb:Functor[BB]) = new Bifunctor[ETO_BI[AA,BB]#λ] {
      type F[A,B] = ETO_BI[AA,BB]#λ[A,B] //ElapsedTimeOf[AA[A],BB[B]]
      def bimap[A, B, C, D](fab: F[A, B])(f: A => C, g: B => D): F[C, D] =
        ElapsedTimeOf(fab.value map f, ElapsedTime(fab.elapsed.time map g)) // ElapsedTime constructor shouldn't be needed since ElapsedTime is a functor

      /* From Bifoldable:
      /** Accumulate `A`s and `B`s in some unspecified order. */
      def bifoldMap[A,B,M](fa: F[A, B])(f: A => M)(g: B => M)(implicit F: Monoid[M]): M

      /** Accumulate to `C` starting at the "right".  `f` and `g` may be interleaved. */
      def bifoldRight[A,B,C](fa: F[A, B], z: => C)(f: (A, => C) => C)(g: (B, => C) => C): C
      */

    }

    implicit def unapplyFunctorEto[TC[_[_]],B, A0](implicit TC0: TC[ETO_NEL[B]#λ]) = Unapply.unapplyMA[TC, ETO_NEL[B]#λ, A0]
    implicit def unapplyFunctorEto2[TC[_[_,_]],AA[_], BB[_], A0, B0](implicit TC0: TC[ETO_BI[AA,BB]#λ]) =
      Unapply2.unapplyMAB[TC, ETO_BI[AA,BB]#λ, A0,B0]
  }


  /* Constructors */
  def elapsed(c:Chronon) = ElapsedTime[Chronon](c)
  def elapseds(c:Chronon, cs:Chronon*):ElapsedTime[NEL[Chronon]] = ElapsedTime[NEL[Chronon]](Nel(c, cs:_*))
  def elapsedOf[A,B](a:A,e:ElapsedTime[B]): ElapsedTimeOf[A, B] = ElapsedTimeOf(a,e)
  def ofElapsed[A,B](e:ElapsedTime[B], a:A, as:A*) = ElapsedTimeOf(Nel(a, as:_*),e)

  /** Times the execution of `u` */
  def chronometer[B](u: => B):ElapsedTimeOf[B,Chronon] = {
    val start = Nanos.getTime
    val res = u
    val end = Nanos.getTime
    ElapsedTimeOf(res, elapsed(end - start))
  }

  /** Times the execution of `f` saving its result */
  def chronograph[A,B](f: (A => B)): A => ElapsedTimeOf[B,Chronon] = a => chronometer(f(a))

  import TestHelper.repeatN
  /** Times the execution of `f` `n` times saving `n` */
  def chronographN[A,B](n:Int)(f: (A => B)): A => ElapsedTimeOf[Int,Chronon] = a => {
    chronometer(repeatN(n)(f(a))).copy(value = n)
  }

}

