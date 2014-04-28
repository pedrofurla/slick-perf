package exec

/**
 * Created by pedrofurla on 17/04/14.
 */
/*object Comparisons {
  import slickperf._
  import jpaperf._
  import scalaz.\/
  import Reports._

  final val jpaE = JPA.EclipseLinkJPA
  final val jpaH = JPA.HibernateLinkJPA

  type RunWrapper = (List[Int],List[DbRun]) => \/[Throwable, List[Report]]

  def jpaRunWrapper(jpa: JPA): RunWrapper = (reps:List[Int], runs:List[DbRun]) =>
    MySqlConnection inSchema {
      val r = runs map { _.run(reps) }
      println(s"Closing emFactory of ${jpa.persistenceUnit}")
      jpa.emFactory.close
      r
    }

  val slickRunWrapper: RunWrapper = (reps:List[Int], runs: List[DbRun]) =>
    MySqlConnection inSchema { runs map { _.run(reps) } }

  val totalTimeColors = List("4070A0",/*"89A54E",*/"823333")
  val perRowTimeColors = List("4070FF",/*"89FF4E",*/"FF3333")

  import shapeless._
  import syntax.sized._

  /** Represents a equivalent run from different frameworks, eg Slick inserts and JPA inserts */
  case class Runs[N <: Nat](title:String,dbruns:Sized[Seq[DbRun],N])

  /** Gathers Runs, how many times each run should be repeated, and how to run. */
  case class RunsComparison[N <: Nat](
       repetitions:List[Int],
       runs:List[Runs[N]],
       runWrappers: Sized[Seq[RunWrapper],N]) {

    /* Transposing is necessary since each DbRun has to be in its proper "context". eg. slick with slick, jpa with jpa etc */
    val transposed:List[List[DbRun]] = runs.map{ _.dbruns }.transpose

    /** Drops the first ReportLine of a report. Further info inside `run` */
    def dropFirst(r:Report) = r.copy(lines=r.lines.drop(1))

    def run: List[(String, Throwable \/ List[Report])] = {
      val wrapDbRun = runWrappers.zip(transposed)
      val ls: List[Throwable \/ List[Report]] = wrapDbRun map { case (wrapper, dbrun) => wrapper(repetitions,dbrun) } toList

      import scalaz.syntax.traverse._
      import scalaz.std.AllInstances._ // IDEA says it's not used, but it's a lie

      val rs: List[List[Throwable \/ Report]] = ls map { _.sequence } // IDEA says it's an error, it's not!
      val rs2 : List[Throwable \/ List[Report]] =  rs.transpose map { _.sequenceU }

      /* the first result, usually one row, disrupts the overall view of the plot */
      val firstDropped = rs2 map { _ map { _ map dropFirst } }

      runs.map{ _.title } zip firstDropped
    }

    def amendedRepetitions = repetitions.drop(1)
  }

  //val repetitions = TestHelper.numberOfInserts

  class SlickVsHibernate(val repetitions:List[Int]) {
    val inserts = Runs(
      "Insertion: Slick x EclipseLink x Hibernate",
      Sized(SlickInsert, /*new JPAInsert(jpaE),*/ new JPAInsert(jpaH))
    )

    val queries = Runs(
        "Queries: Slick x EclipseLink x Hibernate",
        Sized(SlickQuery, /*new JPAQuery(jpaE),*/ new JPAInsert(jpaH))
    )

    val comparisons = RunsComparison(
      repetitions,
      List(inserts,queries),
      Sized(slickRunWrapper, /*jpaRunWrapper(jpaE),*/ jpaRunWrapper(jpaH))
    )
  }
}*/

object Comparisons2 {
  import slickperf._
  import jpaperf._
  import scalaz._
  import Scalaz._
  import Chronograph2._

  final val jpaE = JPA.EclipseLinkJPA
  final val jpaH = JPA.HibernateLinkJPA

  val Nel = NonEmptyList
  type Nel[+A] = NonEmptyList[A]

  type Report = ElapsedTimeOf[String, NonEmptyList[Chronon]]

  type RunWrapper = (Nel[Int],List[DbRun]) => \/[Throwable, List[Report]]

  def jpaRunWrapper(jpa: JPA): RunWrapper = (reps:Nel[Int], runs:List[DbRun]) =>
    MySqlConnection inSchema {
      val r = runs map { _.run2(reps) }
      println(s"Closing emFactory of ${jpa.persistenceUnit}")
      jpa.emFactory.close
      r
    }

  val slickRunWrapper: RunWrapper = (reps:Nel[Int], runs: List[DbRun]) =>
    MySqlConnection inSchema { runs map { _.run2(reps) } }

  val totalTimeColors = List("4070A0",/*"89A54E",*/"823333")
  val perRowTimeColors = List("4070FF",/*"89FF4E",*/"FF3333")

  import shapeless._
  import shapeless.syntax.sized._

  /** Represents a equivalent run from different frameworks, eg Slick inserts and JPA inserts */
  case class Runs[N <: Nat](title:String,dbruns:Sized[Seq[DbRun],N])

  /** Gathers Runs, how many times each run should be repeated, and how to run. */
  case class RunsComparison[N <: Nat](
       repetitions:NonEmptyList[Int],
       runs:List[Runs[N]],
       runWrappers: Sized[Seq[RunWrapper],N]) {

    /* Transposing is necessary since each DbRun has to be in its proper "context". eg. slick with slick, jpa with jpa etc */
    val transposed:List[List[DbRun]] = runs.map{ _.dbruns }.transpose

    /** Drops the first ReportLine of a report. Further info inside `run` */
    def dropFirst(r:Report): Report = r.copy(elapsed=ElapsedTime(r.elapsed.time.tail.toNel.get))
    // TODO^^ get rid of the above GET !!!!

    def run: List[(String, Throwable \/ List[Report])] = {
      val wrapDbRun = runWrappers.zip(transposed)
      val ls: List[Throwable \/ List[Report]] = wrapDbRun map { case (wrapper, dbrun) => wrapper(repetitions,dbrun) } toList

      import scalaz.syntax.traverse._
      import scalaz.std.AllInstances._ // IDEA says it's not used, but it's a lie

      val rs: List[List[Throwable \/ Report]] = ls map { _.sequence } // IDEA says it's an error, it's not!
      val rs2 : List[Throwable \/ List[Report]] =  rs.transpose map { _.sequenceU }

      /* the first result, usually one row, disrupts the overall view of the plot, so we skip it */
      val firstDropped =
        rs2 map { maybeReports =>
        maybeReports map { report =>
        report map dropFirst } }

      runs.map{ _.title } zip firstDropped
    }

    def amendedRepetitions = repetitions.tail.toNel.get
    // TODO^^ get rid of the above GET !!!!
  }

  class SlickVsHibernate(val repetitions:Nel[Int]) {
    val inserts = Runs(
      "Insertion: Slick x EclipseLink x Hibernate",
      Sized(SlickInsert, /*new JPAInsert(jpaE),*/ new JPAInsert(jpaH))
    )

    val queries = Runs(
        "Queries: Slick x EclipseLink x Hibernate",
        Sized(SlickQuery, /*new JPAQuery(jpaE),*/ new JPAQuery(jpaH))
    )

    val comparisons = RunsComparison(
      repetitions,
      List(inserts,queries),
      Sized(slickRunWrapper, /*jpaRunWrapper(jpaE),*/ jpaRunWrapper(jpaH))
    )
  }
}
