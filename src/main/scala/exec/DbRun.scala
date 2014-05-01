package exec

/**
 * Created by pedrofurla on 17/04/14.
 */

trait DbRun {
  //import exec.Reports._

  //def run(repetitions:List[Int]):Report

  import exec.Chronometer._
  import Comparisons.Report

  /** A run takes a list of repetitions and returns a resport.  */
  def run(repetitions:NEL[Int]):Report
}
