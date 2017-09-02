import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._

import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {

  /** The name of your application */
  val name = "scalajs-spa"

  /** The version of your application */
  val version = "0.0.1"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.12.2"
    val scalaDom = "0.9.2"
    val scalajsReact = "1.0.1"
    val scalajsReactComponents = "0.7.0"
    val scalaCSS = "0.5.3"
    val log4js = "1.4.14"
    val autowire = "0.2.6"
    val booPickle = "1.2.5"
    val diode = "1.1.2"
    val uTest = "0.4.7"
    val react = "15.5.4"
    val jQuery = "1.11.1"
    val bootstrap = "3.3.6"
    val chartjs = "2.1.3"
    val scalajsScripts = "1.1.1"
    val playJson = "2.6.0"

    // js dependencies
    val MuiVersion   = "0.17.0"
    val reactVersion = "15.4.2"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(
      Seq(
        "com.lihaoyi" %%% "autowire" % versions.autowire,
        "me.chrons" %%% "boopickle" % versions.booPickle
      ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(
      Seq(
        "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
        "com.typesafe.play" %% "play-json" % versions.playJson,
        "org.webjars" % "font-awesome" % "4.3.0-1" % Provided,
        "org.webjars" % "bootstrap" % versions.bootstrap % Provided,
        "com.lihaoyi" %% "utest" % versions.uTest % Test
      ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(
      Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
        "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
        "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
        "com.olvind" %%% "scalajs-react-components" % versions.scalajsReactComponents,
        "io.suzaku" %%% "diode" % versions.diode,
        "io.suzaku" %%% "diode-react" % versions.diode,
        "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
        "com.lihaoyi" %%% "utest" % versions.uTest % Test
      ))
}
