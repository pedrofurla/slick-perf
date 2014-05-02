import twirl.sbt.TwirlPlugin.Twirl
import twirl.sbt.TwirlPlugin.Twirl._

version := "1.0"

scalaVersion := "2.11.0"


//addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.8")

//scalacOptions in (Compile, compile) += "-P:wartremover:traverser:org.brianmckenna.wartremover.warts.Unsafe"


// ************** DEPENDENCIES

resolvers in ThisBuild ++= Seq(
  "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

// libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

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

// ************** Runtime config

fork in run := true

fork in console := true

//javaHome in run := Option(file("/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home"))

javaOptions in run ++= Seq(
  "-Dorg.jboss.logging.provider=slf4j",
  "-Xmx4096m", "-XX:MaxPermSize=512m", "-Xss4M"
  //, "-XX:+UseConcMarkSweepGC"
  //, "-XX:+CMSClassUnloadingEnabled"
  //,"-Xdebug"
  //, "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
  //,"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
)

initialCommands in console :=
  """
    |import exec.TestHelper._
    |import exec.Chronometer._
    |//import exec.Comparisons._
    |import slickperf._
    |import support.SlickInstances.SlickMySql
    |import support.SlickInstances.SlickMySql._
    |import SlickMySql.simple._
    |//import exec.CSV._
    |
    |//import shapeless.{Id => _, _}
    |import experiments.ScalazExperiments.Other._
    |import exec.RunCharts._
    |import scalaz._
    |import Scalaz._
    |
    |val slick = SlickMySql
  """.stripMargin



initialCommands in Test :=
  """
    |import experiments.ScalazExperiments.Other._
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
    "exec.Comparisons._",
    "scalaz._",
    "Scalaz._")


//val hconsole = taskKey[Unit]("")

//hconsole <<= Defaults.consoleTask(fullClasspath in Compile, console in Compile) // (console in compile)



//(console in hconfig) <<= (console in Compile) map { x => initialCommands( _ => "bunda" ) }
//
//  initialCommands in hconfig :=
//  """
//  |import exec._
//  |import exec.TestHelper._
//  |import support.JpaInstances.HibernateJpa
//  |import scalaz._
//  |import Scalaz._
//  |
//  |import jpaperf._
//  |import jpaperf.entities._
//  |
//  |val slick = support.SlickInstances.SlickMySql
//  |slick.createSchema
//  |
//  |val jpa = HibernateJpa
//  |val ic = new JapInsertCompany(jpa)
//  |ic.run(Nel(10))
//  """.stripMargin
//
//  cleanupCommands in hconfig  :=
//  """
//  |slick.destroySchema
//  """.stripMargin


val hconfig = config("hconfig").extend(Compile)

// TODO Now that I am using the right config '.configTasks' would the below commented version work?
val slickperf = project.in(file(".")).
  configs(hconfig).
  settings(inConfig(hconfig)(Defaults.configTasks) : _*)
  .settings(
      fork.in(hconfig,console) := true,
      initialCommands in hconfig :=
        """
        |import exec._
        |import exec.TestHelper._
        |import support.JpaInstances.HibernateJpa
        |import scalaz._
        |import Scalaz._
        |
        |import collection.JavaConversions._
        |
        |import jpaperf._
        |import jpaperf.Entities._
        |
        |val slick = support.SlickInstances.SlickMySql
        |slick.createSchema
        |
        |val jpa = HibernateJpa
        |val em = jpa.defaultEm
        |val ic = new JpaInsertCompanyEmployee(jpa)
        |ic.run(Nel(3))
        """.stripMargin,
      cleanupCommands in hconfig :=
        """
        |jpa.emFactory.close()
        |slick.destroySchema
        """.stripMargin
  )

//val confed = inConfig(hconfig)(Defaults.defaultSettings ++ Defaults.configSettings ++ Seq(
//  initialCommands :=
//  """
//  |import exec._
//  |import exec.TestHelper._
//  |import support.JpaInstances.HibernateJpa
//  |import scalaz._
//  |import Scalaz._
//  |
//  |import jpaperf._
//  |import jpaperf.entities._
//  |
//  |val slick = support.SlickInstances.SlickMySql
//  |slick.createSchema
//  |
//  |val jpa = HibernateJpa
//  |val ic = new JapInsertCompany(jpa)
//  |ic.run(Nel(10))
//  """.stripMargin,
//  cleanupCommands :=
//  """
//  |slick.destroySchema
//  """.stripMargin
//))

 //thisProject.value.settings


//scalacOptions in Compile += "-explaintypes"
//scalacOptions in Compile += "-Xlog-implicits"

//scalacOptions in test += "-explaintypes"

//scalacOptions in test += "-Xlog-implicits"