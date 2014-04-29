package experiments

import exec.Chronometer._

/**
 * Created by pedrofurla on 19/04/14.
 */
object ScalazExperiments {

  type Chronon = Long
  import scalaz.NonEmptyList
  type NEL[+A] = NonEmptyList[A]
  val Nel = NonEmptyList
  
  object Other {
    import scalaz._
    import Scalaz._
  
    type ET_CC[CC[_]] = {
      type λ[A]=ElapsedTime[CC[A]]
    }

    type ETO_NEL_NEL[B] = { type λ[A]=ElapsedTimeOf[NEL[A], NEL[B]] }
    type ETO_NEL[B] = { type λ[A]=ElapsedTimeOf[NEL[A], B] }
    type ETO_BI[AA[_],BB[_]] = { type λ[A,B]=ElapsedTimeOf[AA[A], BB[B]] }

    /** Elapsed time for a given time unit in nano seconds*/
    case class ElapsedTime[A](time: A) {
      def nel/*(implicit ev: A =!= NEL[_])*/ = ElapsedTime(Nel.nels(time))
    }
    trait LowPriorityElapsedTime {
      implicit def isApplicative_Id = new Applicative[ElapsedTime] {
        def point[A](a: => A): ElapsedTime[A]= ElapsedTime[A](a)
        def ap[A,B](fa: => ElapsedTime[A])(f: => ElapsedTime[A => B]): ElapsedTime[B] =
          point(f.time(fa.time))
      }
      //implicit def unapplyMALocalLow[TC[_[_]], A0](implicit TC0: TC[ET_CC[Id]#λ]) = Unapply.unapplyMA[TC, ET_CC[Id]#λ, A0]
    }
    object ElapsedTime /*extends LowPriorityElapsedTime*/ {

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
    object TestEt {
      implicitly[Monoid[ElapsedTime[Int]]]
      implicitly[Semigroup[ElapsedTime[NEL[Int]]]]
      implicitly[Functor[ET_CC[NEL]#λ]]
      implicitly[Applicative[ET_CC[NEL]#λ]]
      implicitly[Foldable[ET_CC[NEL]#λ]]
      implicitly[Foldable1[ET_CC[NEL]#λ]]

      //implicitly[Applicative[ET_CC[Id]#λ]]

      val sample: ElapsedTime[NEL[Long]] = ElapsedTime(Nel(1L,2L,3L))

      sample.map{_ + 1 }
      //ElapsedTime(1) map { _ + 1 }

      val sample2: NEL[ElapsedTime[Long]] = sample.time map { ElapsedTime(_) }
      val sample3: NEL[ElapsedTime[NEL[Long]]] = sample.time map {x => Applicative[ET_CC[NEL]#λ].point(x) }  // { x => ElapsedTime(NEL.nels(x)) }
      // val sample4: NonEmptyList[ElapsedTime[Long]] = sample.time map {x => Applicative[ET_CC[Id]#λ].point(x) }
      //val sample4: ElapsedTime[NEL[Long]] = sample3.traverse(n => ElapsedTime(n.time))
    }

    case class ElapsedTimeOf[A,B](value:A, elapsed: ElapsedTime[B]) {
      def nel = this.copy(value = Nel.nels(value))
      def nelnel = this.copy(value = Nel.nels(value), elapsed = elapsed.nel)
    }

    object ElapsedTimeOf {
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
        def point[A](a: => A): F[A] = ElapsedTimeOf(Nel.nels(a), met.zero)
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
      /*implicit def EtoPimps4[AA[_]](implicit fa:Functor[AA], fb:Functor[ElapsedTime]) = new Bifunctor[ETO_BI[AA,ElapsedTime]#λ] {
        type F[A,B] = ETO_BI[AA,ElapsedTime]#λ[A,B] //ElapsedTimeOf[AA[A],BB[B]]
        def bimap[A, B, C, D](fab: F[A, B])(f: A => C, g: B => D): F[C, D] =
          ElapsedTimeOf(fab.value map f, ElapsedTime(fab.elapsed.time map g)) // ElapsedTime constructor shouldn't be needed since ElapsedTime is a functor
      }*/
/*
      implicit def isFunctor_NEL_B[ETB] = new Functor[ETO_NEL[ETB]#λ] {
        def map[A,B](fa: ElapsedTimeOf[NEL[A],ETB])(f: A => B): ElapsedTimeOf[NEL[B],ETB] = fa.copy(value=fa.value.map(f))
      }
      implicit def isFunctor_NEL_NEL[ETB] = new Functor[ETO_NEL_NEL[ETB]#λ] {
        def map[A,B](fa: ElapsedTimeOf[NEL[A],NEL[ETB]])(f: A => B): ElapsedTimeOf[NEL[B],NEL[ETB]] = fa.copy(value=fa.value.map(f))
      }*/

      //implicit def unapplyMA[TC[_[_]], M0[_], A0](implicit TC0: TC[M0]): Unapply[TC, M0[A0]]
      implicit def unapplyFunctorEto[TC[_[_]],B, A0](implicit TC0: TC[ETO_NEL[B]#λ]) = Unapply.unapplyMA[TC, ETO_NEL[B]#λ, A0]

      //implicit def unapplyMAB[TC[_[_, _]], M0[_, _], A0, B0](implicit TC0: TC[M0]): Unapply2[TC, M0[A0, B0]]
      implicit def unapplyFunctorEto2[TC[_[_,_]],AA[_], BB[_], A0, B0](implicit TC0: TC[ETO_BI[AA,BB]#λ]) =
        Unapply2.unapplyMAB[TC, ETO_BI[AA,BB]#λ, A0,B0]

      /*implicit class ElapsedTimeOfOps[A](val eto:ElapsedTimeOf[A,Id]) {
        def toMany:ElapsedTimeOf[NEL[A],NEL] = elapsedOf(NEL(eto.value), eto.elapsed.toMany)
      }*/
    }

    object TestEto {

      implicitly[Semigroup[ElapsedTimeOf[Int,NEL[Int]]]]
      implicitly[Semigroup[ElapsedTimeOf[NEL[Int],NEL[Int]]]]
      implicitly[Monoid[ElapsedTimeOf[Int,Int]]]
      implicitly[Semigroup[ElapsedTimeOf[NEL[Int],Int]]]

      implicitly[Functor[ETO_NEL[Int]#λ]]
      implicitly[Functor[ETO_NEL_NEL[Int]#λ]]

      implicitly[Bifunctor[ETO_BI[NEL,NEL]#λ]]
      //implicitly[Bifunctor[ETO_BI[NEL,Id]#λ]]


      val ETO = ElapsedTimeOf

      ETO(Nel(1,2,3),elapseds(1,2,3)).map(_ + 1)
      ETO(Nel(1,2,3),elapsed(1)).map(_ + 1)

      ETO(Nel.nels(1,2,3),elapseds(1,2,3)).bimap(_ + 1, _ + 1)
      //ETO(NEL.nels(1,2,3),elapsed(1)).bimap(_ + 1, _ + 1)
      //val x = ETO(NEL.nels(1,2,3),elapsed(1)).sequence

    }

    def elapsed(c:Chronon) = ElapsedTime[Chronon](c)
    def elapseds(c:Chronon, cs:Chronon*):ElapsedTime[NEL[Chronon]] = ElapsedTime[NEL[Chronon]](Nel(c, cs:_*))
    def elapsedOf[A,B](a:A,e:ElapsedTime[B]) = ElapsedTimeOf[A,B](a,e)
    def ofElapsed[A,B](e:ElapsedTime[B], a:A, as:A*) = ElapsedTimeOf(Nel(a, as:_*),e)

    case class XXX[A, Phantom](value:A)
    object XXX {
      
      trait PhantomId
      trait PhantomCC
      
      type XXX_A = { type λ[A]=XXX[A, PhantomId] }
      type XXX_CC[CC[_], Phantom] = { type λ[A]=XXX[CC[A],Phantom] }

      /*implicit def isFunctor2 = new Functor[XXX] { //isFunctor[XXX_CC[Id]#λ]
        def map[A,B](fa: XXX[A])(f: A => B): XXX[B] = fa.copy(value=f(fa.value))
      }*/

      implicit def isFunctor3:Functor[XXX_CC[Id, PhantomId]#λ] = new Functor[XXX_CC[Id, PhantomId]#λ] {
        type CC[A] = Id[A]
        def map[A,B](fa: XXX[CC[A],PhantomId])(f: A => B): XXX[CC[B],PhantomId] = fa.copy(value=fa.value.map(f))
      }
      implicit def isFunctor[CC[_]:Functor] = new Functor[XXX_CC[CC,PhantomCC]#λ] {
        def map[A,B](fa: XXX[CC[A],PhantomCC])(f: A => B): XXX[CC[B],PhantomCC] = fa.copy(value=fa.value.map(f))
      }
      /*implicit def isFunctor:Functor[XXX_CC[NEL]#λ] = new Functor[XXX_CC[NEL]#λ] {
        def map[A,B](fa: XXX[NEL[A]])(f: A => B): XXX[NEL[B]] = fa.copy(value=fa.value.map(f))
      }*/

      //implicit def unapplyMA[TC[_[_]], M0[_], A0](implicit TC0: TC[M0]): Unapply[TC, M0[A0]]
      //implicit def unapplyFunctorXXX[TC[_[_]],CC[_], A0](implicit TC0: TC[XXX_CC[CC,PhantomId]#λ]) = Unapply.unapplyMA[TC, XXX_CC[CC,PhantomId]#λ, A0]
      implicit def unapplyFunctorXXX[TC[_[_]],CC[_], P, A0](implicit TC0: TC[XXX_CC[CC,P]#λ]) = Unapply.unapplyMA[TC, XXX_CC[CC,P]#λ, A0]

      implicitly[Functor[XXX_CC[NEL,PhantomCC]#λ]]

      XXX[NEL[Int],PhantomCC](Nel(1,2,3)).map(_ + 1)


      implicitly[Functor[XXX_CC[Id,_]#λ]]
    }

    object TestXXX {
      import Unapply._

      //import XXX.unapplyFunctorEt
      val X = XXX

      implicitly[Functor[X.XXX_CC[NEL,X.PhantomCC]#λ]]

      X[NEL[Int],X.PhantomCC](Nel(1,2,3)).map(_ + 1)

      X[Int,X.PhantomId](1).map(_ + 1)

      //implicitly[Foldable1[X.XXX_NEL#λ]]
    }
    
    
    
  }
  object YetAnother {
    type Chronon = Long
    import scalaz._
    import Scalaz._
    import scalaz.syntax._
    case class ElapsedTime[A, CC[_]](time: CC[A])//(implicit ev:A =:= Chronon)
      object ElapsedTime {
        import scalaz.syntax._
        import scalaz.syntax.functor._

        implicit def canBeMonoid[CC[_]](implicit m:Monoid[CC[Chronon]]):Monoid[ElapsedTime[Chronon,CC]] = {
          Monoid.instance[ElapsedTime[Chronon,CC]](
            (e1,e2) => e1.copy(time = e1.time |+| e2.time),
            ElapsedTime[Chronon,CC](m.zero)
          )
        }

        type O[CC[_]] = {
          type λ[A]=ElapsedTime[A, CC]
        }

        implicit def canBeFunctor[CC[_]](implicit evf: Functor[CC]) = new Functor[O[CC]#λ] {
          def map[A,B](fa: ElapsedTime[A,CC])(f: A => B): ElapsedTime[B,CC] = ElapsedTime[B,CC](evf.map(fa.time)(f))
        }

        implicit class ElapsedTimeOps(val et:ElapsedTime[Chronon,Id]) extends AnyVal {
          //def toMany: ElapsedTime[Chronon, List] = elapseds(et.time)
        }
      }
  }

  object YetAnother2 {
    import scalaz._
    import Scalaz._
    case class ElapsedTime[A, CC[_]](time: CC[A])
    object ElapsedTime {
      type ET_A[CC[_]] = {
        type λ[A]=ElapsedTime[A, CC]
      }

      type ET_A_NEL = {
        type λ[A]=ElapsedTime[A, NEL]
      }

      implicit def canBeFunctor[CC[_]](implicit evf: Functor[CC]) = new Functor[ET_A[CC]#λ] {
        def map[A,B](fa: ElapsedTime[A,CC])(f: A => B): ElapsedTime[B,CC] = ElapsedTime[B,CC](evf.map(fa.time)(f))
      }

      implicit def unapplyFunctorEt[TC[_[_]],CC[_], A0](implicit TC0: TC[ET_A[CC]#λ]) = Unapply.unapplyMA[TC, ET_A[CC]#λ, A0]

      implicitly[Functor[ET_A_NEL#λ]]

      implicitly[Functor[ET_A[NEL]#λ]]

      ElapsedTime(Nel(1,2,3)).map(_+1)



    }

    ElapsedTime(Nel(1,2,3)).map(_+1)
  }

}
