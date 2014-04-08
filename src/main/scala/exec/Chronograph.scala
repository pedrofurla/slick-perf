package exec

/**
 * Created by pedrofurla on 31/03/14.
 */
object Chronograph {

  /** Chronon is the quantum of time, here in our silly computers it got be a Long */
  type Chronon = Long

  sealed case class TimeUnit(chrononsInSecond: Chronon, name:String, final val _getTime:() => Chronon) {
    // hack 'cause val parameters can't be by-name!
    final def getTime = _getTime()
  }
  val Nanos = new TimeUnit(1e9.toLong, "ns", () => System.nanoTime)
  val Micros = new TimeUnit(1e6.toLong, "μs", () => System.nanoTime/1000)
  val Millis = new TimeUnit(1e3.toLong, "ms", () => System.nanoTime()/1000000)

  case class ElapsedTimeOf[B](value:B, time: Chronon, unit:TimeUnit)
  case class ElapsedTime(time: Chronon, unit:TimeUnit)

  /** Times the execution of `u` */
  def chronometer[B](u: => B): TimeUnit => ElapsedTimeOf[B] = (timer: TimeUnit) => {
    val start = timer.getTime
    val res = u
    val end = timer.getTime
    ElapsedTimeOf(res, end - start, timer)
  }

  /** Times the execution of `f` */
  def chronograph[A,B](timer: TimeUnit)(f: (A => B)): A => ElapsedTimeOf[B] = a => chronometer(f(a))(timer)

  def nanos[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Nanos)(f)
  def micros[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Micros)(f)
  def millis[A,B](f:A => B): A => ElapsedTimeOf[B] = chronograph(Millis)(f)
}

object Reports {
  import Chronograph._

  import scalaz.Show._
  import scalaz.Show

  case class ReportLine(rows:Int, time:Chronograph.Chronon, timeUnit:TimeUnit) {
    val timePerRow:Float = time/rows.toFloat
  }

  case class Report(title:String, lines:List[ReportLine], timeUnit:Chronograph.TimeUnit) {
    val totalRows = lines map {_.rows} sum
    val totalTime = lines map {_.time} sum
    val totalAvgTime = totalTime / totalRows.toFloat
  }

  implicit val reportline2show: Show[ReportLine] = shows { rl =>
    val unitName = rl.timeUnit.name
    s"# of Rows: ${rl.rows}, totalTime: ${rl.time} $unitName, time per row: ${rl.timePerRow} $unitName"
  }
  implicit val report2show: Show[Report] = shows { r =>
    val unitName = r.timeUnit.name
    s"Total Rows: ${r.totalRows}, totalTime: ${r.totalTime} $unitName, average time per row: ${r.totalAvgTime} $unitName"
  }

  def reportLine(e:ElapsedTimeOf[Int]): ReportLine = e match {
    case ElapsedTimeOf(rows, time, t@TimeUnit(_, name, _)) =>
      //println(s"# of Rows: $rows, totalTime: $time $name, time per row: ${time/rows.toFloat} $name")
      ReportLine(rows, time, t)
  }

}

