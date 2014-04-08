
version := "1.0"

scalaVersion := "2.10.3"

resolvers in ThisBuild ++= Seq(
  "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

//addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.8")

//scalacOptions in (Compile, compile) += "-P:wartremover:traverser:org.brianmckenna.wartremover.warts.Unsafe"

// libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

val commonsDeps = Seq(
    "com.typesafe.slick" %% "slick" % "2.0.1",
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    //"com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.0.1", // 2.11 only
    //"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
    //"org.slf4j" % "slf4j-simple" % "1.6.4",
    //"ch.qos.logback" % "logback-classic" % "1.0.13",
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "com.chuusai" % "shapeless" % "2.0.0-M1" cross CrossVersion.full
)

val hibernateDeps = Seq(
    //"org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final" ,
    "org.hibernate" % "hibernate-entitymanager" % "4.3.5.Final")

val eclipselinkDeps = Seq("org.eclipse.persistence" % "eclipselink" % "2.5.0")


libraryDependencies ++=
  commonsDeps ++
  eclipselinkDeps
  //hibernateDeps

fork in run := true

initialCommands in console :=
  """
    |import exec.TestHelper._
    |import exec.Chronograph._
    |//import exec.Report
    |//import exec.Report._
    |import slickperf._
    |import MySqlConnection._
    |import MySqlConnection.simple._
    |import shapeless._
    |import exec.CSV._
  """.stripMargin