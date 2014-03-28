
version := "1.0"

resolvers in ThisBuild ++= Seq(
  "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/",
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

// libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "2.0.1",
    "org.eclipse.persistence" % "eclipselink" % "2.5.0",
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
    "org.slf4j" % "slf4j-nop" % "1.6.4")

fork in run := true

initialCommands in console :=
  """
    |import slickperf._
    |import MySqlConnection._
    |import MySqlConnection.simple._
  """.stripMargin