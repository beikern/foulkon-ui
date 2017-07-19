package client

import chandu0101.scalajs.react.components.ReactTapEventPlugin
import chandu0101.scalajs.react.components.materialui.MuiMuiThemeProvider
import client.appstate.SPACircuit
import client.components.{CountAndFilterToolBar, GlobalStyles, NavToolBar}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

@JSExportTopLevel("SPAMain")
object SPAMain extends JSApp {
  // This plugin MUST be in scope!!! if not MUI will not work as expected.
  ReactTapEventPlugin(js.undefined)

  // Define the locations (pages) used in this application
  sealed trait Location

  case object DashboardLocation extends Location
  case object UserLocation extends Location

  // Configure the router
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location].buildConfig { dsl =>
      import dsl._

    val userWrapper = SPACircuit.connect(_.users)


    (staticRoute(root, DashboardLocation) ~>
      renderR(
        ctl =>
          <.div(
            MuiMuiThemeProvider()(CountAndFilterToolBar("Users")),
            MuiMuiThemeProvider()(CountAndFilterToolBar("Users2")),
             <.div("Hello World with Play 2.6.0")
          )
      )
    ).notFound(redirectToPage(DashboardLocation)(Redirect.Replace))
  }.renderWith(layout)

  def layout(c: RouterCtl[Location], r: Resolution[Location]) = {
    val appBar = MuiMuiThemeProvider()(NavToolBar("Foulkon UI"))

    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar, navbar-fixed-top",
        <.div(^.className := "container", appBar)
        )
      ,
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)

    // tell React to render the router in the document body
    router.mapUnmounted(_.renderIntoDOM(dom.document.getElementById("root")))
  }
}
