
/**
 In the lack of better place I am adding a few aliases and shortcuts here.
 As a convention, type constructors are always all upper case, value constructors are always upper camel case.
*/
package object exec {
  import scalaz._
  import Scalaz._

  type NEL[+A] = NonEmptyList[A]
  val Nel = NonEmptyList


}