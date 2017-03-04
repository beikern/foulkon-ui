lazy val `scala-js-spa` =
  project.in(file(".")).enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  Library.scalaTest % "test"
)

initialCommands := """|import es.beikern.scala.js.spa._
                      |""".stripMargin
