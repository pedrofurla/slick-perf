package exec

/**
 * Created by pedrofurla on 17/04/14.
 */

trait DbRun {
  //import exec.Reports._

  //def run(repetitions:List[Int]):Report

  import exec.Chronograph2._
  def run2(repetitions:NEL[Int]):ElapsedTimeOf[String, NEL[Chronon]]
}
