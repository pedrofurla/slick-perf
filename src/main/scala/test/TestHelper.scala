package client.test



object TestHelper {

  val numberOfInserts = List(1, 10, 20, 30, 40, 50, 100, 200, 300, 400, 600, 800, 1000)

  def printTime(start: Long, end: Long, number: Int) {
    val take = end - start
    val takeSec = take / 1000
    println(s"inserts $number entities for ms $take")
  }


  case class TimeUnit(divisor:Long, name:String)
  val MS = TimeUnit(1, "ms")
  val SEC = TimeUnit(1000, "sec")

  def printTime(time: Long, number:Int=1, timeUnit:TimeUnit = SEC) {
    val duration = time / timeUnit.divisor
    println(s"query executed $number times for $duration ${timeUnit.name}")
  }

  def chronometer(u: => Unit):Long = {
    val start = System.currentTimeMillis()
    val res = u
    val end = System.currentTimeMillis()
    end - start
  }
  
  def execute(times: Int)(block: => Unit){
    for (i <- 1 to times){
      block
    }
  }

}