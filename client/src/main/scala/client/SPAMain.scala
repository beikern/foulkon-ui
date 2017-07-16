package client

import client.appstate.SPACircuit
import client.components.GlobalStyles
import client.logger._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

@JSExportTopLevel("SPAMain")
object SPAMain extends JSApp {

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
            <.div("Hello World with Play 2.6.0")
          )
      )
    ).notFound(redirectToPage(DashboardLocation)(Redirect.Replace))
  }.renderWith(layout)


  def layout(c: RouterCtl[Location], r: Resolution[Location]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Bootstrap components")),
          <.div(^.className := "collapse navbar-collapse")
        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)

    // tell React to render the router in the document body
    router.mapUnmounted(_.renderIntoDOM(dom.document.getElementById("root")))
  }
}
