package slickperf

/**
 * Created by pedrofurla on 28/03/14.
 */
object Main {
  def main(args: Array[String]) {
    MySqlConnection inSchema run
  }

  def run() {
    SlickInsert.run
    SlickQuery.run
  }
}
