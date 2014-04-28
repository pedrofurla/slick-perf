package exec

import slickperf.{SlickInsert, MySqlConnection}
import jpaperf.JPAInsert
//import exec.Reports.Report

/**
 * Created by pedrofurla on 09/04/14.
 */
object RunCharts {

  import java.io._

  import scalaz._

  def saveObject(fileName:String, obj: Any):Unit = {
    val fileOut = new FileOutputStream(fileName);
    val out = new ObjectOutputStream(fileOut);
    out.writeObject(obj);
    out.close();
    fileOut.close();
  }
  def loadObject[T](fileName:String):T = {
    val file = new FileInputStream(fileName);
    val in = new ObjectInputStream(file);
    val obj = in.readObject();
    in.close();
    file.close();
    obj.asInstanceOf[T]
  }

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


  def writeFile(contents: String,filename:String) {
    val writer = new PrintWriter(new File(filename))
    writer.write(contents)
    writer.close()
  }

  /*def run():Unit = {
    import Comparisons._
    val comparison = new SlickVsHibernate(TestHelper.numberOfInserts).comparisons
    val result = comparison.run

    val contents = templates.html.plots2(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output2.html")
  }*/

  def run2():Unit = {
    import Comparisons2._
    import scalaz._
    val comparison = new SlickVsHibernate(NonEmptyList(1,10,20,30,40,50)).comparisons
    val result = comparison.run

    val contents = templates.html.plots3(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output3.html")
  }


  /*def runTest():Unit = {
    import Comparisons._
    val comparison = new SlickVsHibernate(TestHelper.numberOfInserts.take(6)).comparisons
    val result = comparison.run

    val contents = templates.html.plots2(comparison.amendedRepetitions, result).body

    writeFile(contents, "html/output-test.html")
  }*/

  def main(args: Array[String]) {
    //load()
    //runTest()
    run2
  }
}
