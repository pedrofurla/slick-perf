package exec

/**
 * Created by pedrofurla on 31/03/14.
 */

object TestHelper {

  import scalaz.std.function.fix
  import scalaz.EphemeralStream
  import scalaz.EphemeralStream._
  final val EStream: EphemeralStream.type = EphemeralStream
  def infinity[A](x: => A) = fix[EStream[A]](xs => EStream.cons(x, xs))
  def infinity2[A](in: EStream[A]) =
    fix[EStream[A]](xs => in ++ xs)

  import scalaz.Show
  import scalaz.syntax.show._
  def printMe[A:Show](a:A):A = {
    println(a.shows)
    a
  }

  val numberOfInserts = List(1, 10/*, 20, 30, 40, 50, 100, 200, 300, 400, 600, 800, 1000
    , 5000, 10000, 1, 10, 20, 30, 40, 50, 100*/)
  val totalInserts = numberOfInserts.sum
  val allIds:List[Long] = (1 to totalInserts) map { _.toLong} toList

  def repeatN2[A](a: => A) = (n:Int) => for(i <- 1 to n) a;
  def repeatNm[A](n:Int)(a: => A) = for(i <- 1 to n) a;
  def repeatN[A] = repeatNm[A] _

  def const[A,B] = (a:A) => (b:B) => a

  def execute(times: Int)(block: => Unit){
    for (i <- 1 to times){
      block
    }
  }

}