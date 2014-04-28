package exec

/*object Reports {
  import Chronograph._

  case class ReportLine(rows:Int, time:Chronograph.Chronon, timeUnit:TimeUnit) {
    val timePerRow:Float = time/rows.toFloat
  }

  case class Report(title:String, lines:List[ReportLine], timeUnit:Chronograph.TimeUnit) {
    val totalRows = lines map {_.rows} sum
    val totalTime = lines map {_.time} sum
    val totalAvgTime = totalTime / totalRows.toFloat
  }

  import scalaz.Show._
  import scalaz.Show

  implicit val reportline2show: Show[ReportLine] = shows { rl =>
    val unitName = rl.timeUnit.name
    s"# of Rows: ${rl.rows}, totalTime: ${rl.time} $unitName, time per row: ${rl.timePerRow} $unitName"
  }
  implicit val report2show: Show[Report] = shows { r =>
    val unitName = r.timeUnit.name
    s"Total Rows: ${r.totalRows}, totalTime: ${r.totalTime} $unitName, average time per row: ${r.totalAvgTime} $unitName"
  }

  def reportLine(e:ElapsedTimeOf[Int]): ReportLine = e match {
    case ElapsedTimeOf(rows, ElapsedTime(time, t@TimeUnit(_, name))) =>
      //println(s"# of Rows: $rows, totalTime: $time $name, time per row: ${time/rows.toFloat} $name")
      ReportLine(rows, time, t)
  }

}*/


/*object Reports2 {
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
    case ElapsedTimeOf(rows, ElapsedTime(time, t@TimeUnit(_, name))) =>
      //println(s"# of Rows: $rows, totalTime: $time $name, time per row: ${time/rows.toFloat} $name")
      ReportLine(rows, time, t)
  }

}*/
