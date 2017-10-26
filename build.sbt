import sbt.Keys.{version, _}
import sbt.Project.projectToRef

// a special crossProject for configuring a JS/JVM/shared structure
lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := Settings.versions.scala,
    libraryDependencies ++= Settings.sharedDependencies.value
  )
  // set up settings specific to the JS project
  .jsConfigure(_.enablePlugins(ScalaJSWeb, ScalaJSBundlerPlugin))

lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")

lazy val sharedJS = shared.js.settings(name := "sharedJS")

// use eliding to drop some debug code in the production build
lazy val elideOptions = settingKey[Seq[String]]("Set limit for elidable functions")

// instantiate the JS project for SBT with some additional settings
lazy val client: Project = (project in file("client"))
  .settings(
    name := "client",
    version := Settings.version,
    scalaVersion := Settings.versions.scala,
    scalacOptions ++= Settings.scalacOptions,
    mainClass in Compile := Some("client.SPAMain"),
    libraryDependencies ++= Settings.scalajsDependencies.value,
    npmDependencies in Compile := Seq(
      "react"                             -> Settings.versions.reactVersion,
      "react-dom"                         -> Settings.versions.reactVersion,
      "react-addons-create-fragment"      -> Settings.versions.reactVersion,
      "react-addons-css-transition-group" -> Settings.versions.reactVersion,
      "react-addons-pure-render-mixin"    -> Settings.versions.reactVersion,
      "react-addons-transition-group"     -> Settings.versions.reactVersion,
      "react-addons-update"               -> Settings.versions.reactVersion,
      "react-infinite"                    -> Settings.versions.reactInfinite,
      "material-ui"                       -> Settings.versions.MuiVersion,
      "react-tap-event-plugin"            -> "2.0.1",
      "jquery"                            -> Settings.versions.jQuery,
      "bootstrap"                         -> Settings.versions.bootstrap
    ),
    npmDevDependencies in Compile += "expose-loader" -> "0.7.1",
    webpackConfigFile := Some(baseDirectory.value/"webpack.config.js"),
    scalaJSUseMainModuleInitializer := true,
    scalaJSUseMainModuleInitializer.in(Test) := false,
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv,
    webpackBundlingMode := BundlingMode.LibraryOnly()
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalaJSWeb)
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

// lazy val root = (project in file(".")).aggregate(client, server)

// loads the Play server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
