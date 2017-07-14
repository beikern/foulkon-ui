import org.scalajs.core.tools.io.{FileVirtualJSFile, VirtualJSFile}
import sbt.Keys.{version, _}
import sbt.Project.projectToRef

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(npmSettings)
  .settings(
    name := "client",
    libraryDependencies ++= Settings.scalajsDependencies.value,
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    mainClass in Compile := Some("client.SPAMain"),
    // by default we do development build, no eliding
    elideOptions := Seq(),
    skip in packageJSDependencies := false,
    scalacOptions ++= elideOptions.value,
    version in webpack := "2.6.1",
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer.in(Test) := false,
    webpackEmitSourceMaps := false,
    enableReloadWorkflow := true
  )
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
  .dependsOn(sharedJS)

// Client projects (just one in this case)
lazy val clients = Seq(client)

// instantiate the JVM project for SBT with some additional settings
lazy val server = (project in file("server"))
  .settings(
    name := "server",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Settings.jvmDependencies.value ++ Seq(guice),
    scalaJSProjects := Seq(client),
    commands += ReleaseCmd,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    // connect to the client project
    scalaJSProjects := clients,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // Expose as sbt-web assets some files retrieved from the NPM packages of the `client` project
    npmAssets ++= NpmAssets.ofProject(client) { modules => (modules / "font-awesome").*** }.value,
    // compress CSS
    LessKeys.compress in Assets := true
  )
  .enablePlugins(PlayScala, WebScalaJSBundlerPlugin)
  .disablePlugins(PlayLayoutPlugin) // use the standard directory layout instead of Play's custom
  .aggregate(clients.map(projectToRef): _*)
  .dependsOn(sharedJVM)

// Command for building a release
lazy val ReleaseCmd = Command.command("release") { state =>
  "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
  "client/clean" ::
  "client/test" ::
  "server/clean" ::
  "server/test" ::
  "server/dist" ::
  "set elideOptions in client := Seq()" ::
  state
}

// Settings
lazy val npmSettings = Seq(
  useYarn := true,
  npmDependencies in Compile := Seq(
    "elemental"                         -> Settings.versions.EuiVersion,
    "highlight.js"                      -> "9.9.0",
    "material-ui"                       -> Settings.versions.MuiVersion,
    "react"                             -> Settings.versions.reactVersion,
    "react-dom"                         -> Settings.versions.reactVersion,
    "react-addons-create-fragment"      -> Settings.versions.reactVersion,
    "react-addons-css-transition-group" -> "15.0.2",
    "react-addons-pure-render-mixin"    -> "15.5.2",
    "react-addons-transition-group"     -> "15.0.0",
    "react-addons-update"               -> "15.5.2",
    "react-geomicons"                   -> "2.1.0",
    "react-infinite"                    -> "0.11.0",
    "react-select"                      -> "1.0.0-rc.5",
    "react-slick"                       -> "0.14.11",
    "react-spinner"                     -> "0.2.7",
    "react-tagsinput"                   -> "3.16.1",
    "react-tap-event-plugin"            -> "2.0.1",
    "semantic-ui-react"                 -> Settings.versions.SuiVersion,
    "svg-loader"                        -> "0.0.2"
  )
)

lazy val npmGenSettings = Seq(
  useYarn := true,
  npmDependencies.in(Compile) := Seq(
    "elemental"         -> Settings.versions.EuiVersion,
    "material-ui"       -> Settings.versions.MuiVersion,
    "semantic-ui-react" -> Settings.versions.SuiVersion
  )
)

// lazy val root = (project in file(".")).aggregate(client, server)

// loads the Play server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
