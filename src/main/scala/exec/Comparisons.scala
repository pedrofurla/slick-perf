package exec

import support._
import jpaperf.old.{JpaQuery, JpaInsert}

/**
 * Created by pedrofurla on 17/04/14.
 */
object Comparisons {
  import scalaz._
  import Scalaz._
  import Chronometer._
  import support.SlickInstances._

  lazy val jpaE = JpaInstances.EclipseLinkJpaOld
  lazy val jpaH = JpaInstances.HibernateJpaOld
  lazy val jpaH2 = JpaInstances.HibernateJpa

  /** A report is a "title" + a Nel of ElapsedTimes */
  type Report = ElapsedTimeOf[String, NEL[Chronon]]

  type RunWrapper = (NEL[Int],List[DbRun]) => \/[Throwable, List[Report]]

  final def jpaRunWrapper(jpa: JpaConnection): RunWrapper = (reps:NEL[Int], runs:List[DbRun]) =>
    SlickMySqlOld inSchema {
      val r = runs map { _.run(reps) }
      println(s"Closing emFactory of ${jpa.persistenceUnit}")
      jpa.emFactory.close
      r
    } leftMap {x => x.printStackTrace(); jpa.emFactory.close; x} // TODO HORRIBLE HACK. FIX ME!

  final def jpaRunWrapper2(jpa: JpaConnection): RunWrapper = (reps:NEL[Int], runs:List[DbRun]) =>
    SlickMySql inSchema {
      val r = runs map { _.run(reps) }
      println(s"Closing emFactory of ${jpa.persistenceUnit}")
      jpa.emFactory.close
      r
    } leftMap {x => x.printStackTrace(); jpa.emFactory.close; x} // TODO HORRIBLE HACK. FIX ME!


  final def slickRunWrapper(slick:SlickSupport with TablesDefinition): RunWrapper = (reps:NEL[Int], runs: List[DbRun]) =>
    slick inSchema { runs map { _.run(reps) } } leftMap {x => x.printStackTrace(); x}          // TODO HORRIBLE HACK. FIX ME!

  val totalTimeColors = List("4070A0",/*"89A54E",*/"823333")
  val perRowTimeColors = List("4070FF",/*"89FF4E",*/"FF3333")

  import shapeless._
  import shapeless.syntax.sized._

  /** Represents a equivalent run from different frameworks, eg Slick inserts and JPA inserts */
  case class Runs[N <: Nat](title:String,dbruns:Sized[Seq[DbRun],N])

  /** Gathers Runs, how many times each run should be repeated, and how to run. */
  case class RunsComparison[N <: Nat](
       repetitions:NEL[Int],
       runs:List[Runs[N]],
       runWrappers: Sized[Seq[RunWrapper],N]) {

    /* Transposing is necessary since each DbRun has to be in its proper "context". eg. slick with slick, jpa with jpa etc */
    val transposed:List[List[DbRun]] = runs.map{ _.dbruns }.transpose

    /** Drops the first result of a report. Further info inside `run` */
    def dropFirst(r:Report): Report = r.copy(elapsed=ElapsedTime(r.elapsed.time.tail.toNel.get))
    // TODO^^ get rid of the above GET !!!!

    def run: List[(String, Throwable \/ List[Report])] = {
      val wrapDbRun = runWrappers.zip(transposed)
      val ls: List[Throwable \/ List[Report]] = wrapDbRun map { case (wrapper, dbrun) => wrapper(repetitions,dbrun) } toList

      import scalaz.syntax.traverse._
      import scalaz.std.AllInstances._ // IDEA says it's not used, but it's a lie

      val rs: List[List[Throwable \/ Report]] = ls map { _.sequence } // IDEA says it's an error, it's not!
      val rs2 : List[Throwable \/ List[Report]] =  rs.transpose map { _.sequenceU }

      /* the first result, usually one row, disrupts the overall view of the plot, so we drop it */
      val firstDropped =
        rs2 map { maybeReports =>
        maybeReports map { report =>
        report map dropFirst } }

      runs.map{ _.title } zip firstDropped
    }

    def amendedRepetitions = repetitions.tail.toNel.get
    // TODO^^ get rid of the above GET !!!!
  }

  class SlickVsHibernateOld(val repetitions:NEL[Int]) {
    import slickperf.old._
    import jpaperf.old._

    val inserts = Runs(
      "Insertion: Slick x EclipseLink x Hibernate",
      Sized(SlickInsert, /*new JPAInsert(jpaE),*/ new JpaInsert(jpaH))
    )

    val queries = Runs(
        "Queries: Slick x EclipseLink x Hibernate",
        Sized(SlickQuery, /*new JPAQuery(jpaE),*/ new JpaQuery(jpaH))
    )

    val comparisons = RunsComparison(
      repetitions,
      List(inserts/*,queries*/),
      Sized(slickRunWrapper(SlickMySqlOld), /*jpaRunWrapper(jpaE),*/ jpaRunWrapper(jpaH))
    )
  }

  class SlickVsHibernate(val repetitions:NEL[Int]) {
    import slickperf._
    import jpaperf._

    val insertsCompany = Runs(
      "Inserting companies: Slick2 x Hibernate",
      Sized(SlickInsertCompany,  new JpaInsertCompany(jpaH2))
    )

    val insertsCompanyEmployee = Runs(
      "Inserting companies and employees: Slick2 x Hibernate",
      Sized(SlickInsertCompanyEmployee,  new JpaInsertCompanyEmployee(jpaH2))
    )

    val queriesCompany = Runs(
      "Querying companies at random",
      Sized(SlickQueryCompany, new JpaQueryCompany(jpaH2))
    )

    /*val queries = Runs(
        "Queries: Slick x EclipseLink x Hibernate",
        Sized(SlickQuery, new JPAQuery(jpaH))
    )*/

    val comparisons = RunsComparison(
      repetitions,
      List(insertsCompany,insertsCompanyEmployee, queriesCompany),
      Sized(slickRunWrapper(SlickMySql),  jpaRunWrapper2(jpaH2))
    )
  }
}
