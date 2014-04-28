package exec

/**
 * Created by pedrofurla on 17/04/14.
 */

trait DbRun {
  //import exec.Reports._

  //def run(repetitions:List[Int]):Report

  import scalaz.NonEmptyList
  import exec.Chronograph2._
  def run2(repetitions:NonEmptyList[Int]):ElapsedTimeOf[String, NonEmptyList[Chronon]]
}
