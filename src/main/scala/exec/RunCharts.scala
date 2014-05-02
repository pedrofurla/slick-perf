package exec

import slickperf.old.SlickInsert
import jpaperf.old.JpaInsert

//import exec.Reports.Report

/**
 * Created by pedrofurla on 09/04/14.
 */
object RunCharts {


  import scalaz._

  /*def save():Unit = {
    val slickRep: \/[Throwable, List[Report]] = MySqlConnection inSchema slickperf.Main.run
    val jpaRep: \/[Throwable, List[Report]] = MySqlConnection inSchema jpaperf.Main.run

    slickRep.map(saveObject("slickperf.ser",_))
    jpaRep.map(saveObject("jpaperf.ser",_))
  }
*/
  /*def load():Unit = {
    val slickRep: \/[Throwable, List[Report]] = \/ right loadObject[List[Report]]("slickperf.ser")
    val jpaRep: \/[Throwable, List[Report]] = \/ right loadObject[List[Report]]("jpaperf.ser")

    val list: \/[Throwable, List[(Report, Report)]] = for { s <- slickRep; j <- jpaRep } yield s zip j

    val contents = templates.html.plots(list.toEither).body

    writeFile(contents, "html/output.html")
  }*/

  /*def run():Unit = {
    import Comparisons._
    val comparison = new SlickVsHibernate(TestHelper.numberOfInserts).comparisons
    val result = comparison.run

    val contents = templates.html.plots2(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output2.html")
  }*/

  import TestHelper.writeFile

  val repetitions = Nel(
  //1
     1,
     10,    20,    30,    40,    50     // 6
    ,100,   200,   300,   400,   500    // 11
    ,1000,  2000,  3000,  4000,  5000   // 16
    //,6000,  7000,  8000,  9000, 10000
    ,10000, 20000, 30000, 40000, 50000  // 21
  )

  def runOld():Unit = {
    import Comparisons._
    import scalaz._
    import Scalaz._
    val reps = repetitions.list.take(17).toNel.get
    val comparison =new SlickVsHibernateOld(reps).comparisons
    val result = comparison.run

    val contents = templates.html.plots3(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output3.html")
  }


  def runTestOld():Unit = {
    import Comparisons._
    import scalaz._
    import Scalaz._
    val reps = repetitions.list.take(16).toNel.get
    val comparison = new SlickVsHibernateOld(reps).comparisons
    val result = comparison.run

    val contents = templates.html.plots3(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output-test.html")
  }

  def runTest():Unit = {
    import Comparisons._
    import scalaz._
    import Scalaz._
    val reps = repetitions.list.take(18).toNel.get
    val comparison = new SlickVsHibernate(reps).comparisons
    val result = comparison.run

    val contents = templates.html.plots3(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output-test-v2.html")
  }


  def main(args: Array[String]) {
    //load()
    //runTest()

    import JvmSpec._
    println(version)
    println(info)
    println(s"arguments: $arguments")

    //runTestOld()
    runTest()

  }
}

object JvmSpec {
  import sys._
  import java.lang.management.ManagementFactory;
  import collection.JavaConversions._

  private val jvmline1 =List("java.runtime.name", "java.runtime.version")
  private val jvmline2 = List("java.vm.name", "java.vm.version","java.vm.info")
  val version = jvmline1.map(props(_)).mkString(" ")
  val info = jvmline2.map(props(_)).mkString(" ")

  private val runtimeMxBean = ManagementFactory.getRuntimeMXBean();
  val arguments = runtimeMxBean.getInputArguments().toList.mkString(" ");
}
