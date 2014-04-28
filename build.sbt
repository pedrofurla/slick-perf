import twirl.sbt.TwirlPlugin.Twirl
import twirl.sbt.TwirlPlugin.Twirl._

version := "1.0"

scalaVersion := "2.11.0"

resolvers in ThisBuild ++= Seq(
  "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

//addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.8")

//scalacOptions in (Compile, compile) += "-P:wartremover:traverser:org.brianmckenna.wartremover.warts.Unsafe"

// libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

// DEPENDENCIES

val shapeless = Seq("com.chuusai" % "shapeless_2.11" % "2.0.0")

val scalaz = Seq("org.scalaz" %% "scalaz-core" % "7.0.6")

val dbs = Seq(
  "mysql" % "mysql-connector-java" % "5.1.25",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41"
)

val slick = Seq(
  //"com.typesafe.slick" %% "slick" % "2.0.1"
  "com.typesafe.slick" % "slick_2.11.0-RC4" % "2.1.0-M1"
)

val logging = Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4"
  //,"com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.0.1" // 2.11 only
  //,"com.typesafe" %% "scalalogging-slf4j" % "1.0.1"
  //,"org.slf4j" % "slf4j-simple" % "1.6.4"
  //,"ch.qos.logback" % "logback-classic" % "1.0.13"
)

val jpaApi = Seq("org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final")

val hibernateDeps = Seq("org.hibernate" % "hibernate-entitymanager" % "4.3.5.Final")

val eclipseLink = Seq("org.eclipse.persistence" % "eclipselink" % "2.5.0")

val hibernateEntityManager = Seq("org.hibernate" % "hibernate-entitymanager" % "4.3.5.Final")

val hibernateCore = Seq("org.hibernate" % "hibernate-core" % "4.3.5.Final")

val testingDeps = Seq(
  //"org.specs2" %% "specs2" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test")

libraryDependencies ++=
  testingDeps ++
  shapeless ++ scalaz ++ dbs ++ logging ++
  slick ++
  //jpaApi ++
  eclipseLink ++
  hibernateEntityManager ++
  hibernateCore // for schemaExport

fork in run := true

fork in console := true

//javaHome in run := Option(file("/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home"))

javaOptions in run ++= Seq(
  "-Dorg.jboss.logging.provider=slf4j"
  //,"-Xdebug"
  //, "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
  //,"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
)

initialCommands in console :=
  """
    |import exec.TestHelper._
    |//import exec.Chronograph._
    |import exec.Chronograph2._
    |//import exec.Comparisons._
    |//import exec.Report
    |//import exec.Report._
    |import slickperf._
    |import MySqlConnection._
    |import MySqlConnection.simple._
    |//import exec.CSV._
    |
    |//import shapeless.{Id => _, _}
    |import experiments.ScalazExperiments.Other._
    |import exec.RunCharts._
    |import scalaz._
    |import Scalaz._
    |
    |//val e = elapsed _
    |//val eo = elapsedOf[Chronon, Id] _
    |//val l = List(elapsedOf(1,elapsed(1)),elapsedOf(1,elapsed(1)))
  """.stripMargin

initialCommands in Test :=
  """
    |import experiments.ScalazExperiments.Other._
    |import exec.ChronographTest2._
    |import scalaz._
    |import Scalaz._
    |
    |//val e = elapsed _
    |//val eo = elapsedOf[Chronon, Id] _
    |//val l = List(elapsedOf(1,elapsed(1)),elapsedOf(1,elapsed(1)))
  """.stripMargin

seq(Twirl.settings: _*)

twirlImports ++=
  Seq(//"exec.Reports._",
    "exec.Comparisons2.Report",
    "scalaz._",
    "Scalaz._")


//scalacOptions in Compile += "-explaintypes"

//scalacOptions in Compile += "-Xlog-implicits"

//scalacOptions in test += "-explaintypes"

//scalacOptions in test += "-Xlog-implicits"