package client

import client.appstate.SPACircuit
import client.components.GlobalStyles
import client.components.bootstrap.buttons.{Button, ButtonGroup, ButtonLoading}
import client.components.bootstrap.navigation._
import client.components.bootstrap.pagelayouts.lists.{ListGroup, ListGroupItem}
import client.components.bootstrap.pagelayouts.panels.Panel
import client.components.bootstrap.pagelayouts.tables.Table
import client.components.bootstrap.styles.BsStyle
import client.logger._
import client.modules.UserModule
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.collection.immutable.ListMap
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

@JSExportTopLevel("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Location

  case object DashboardLocation extends Location
  case object UserLocation extends Location
  val header = NavbarHeader(NavbarHeader.Props())
  val nav = Nav(Nav.Props(), NavItem(NavItem.Props(), "Link"))
  val navBar = NavBar(NavBar.Props(inverse = true), nav)
  val bootstrapButton = Button(Button.Props(BsStyle.primary, Callback{println("wonderful!")}), "hello")
  val buttonList =
    List(
      Button(Button.Props(BsStyle.info, Callback{println("wonderful!")}), "info"),
      Button(Button.Props(BsStyle.danger, Callback{println("wonderful!")}), "error!")
    )
  val groupedButtons = ButtonGroup(ButtonGroup.Props(block = false, justified = false, vertical = false))(buttonList.map(_.vdomElement):_ *)
  val tableHead =
    <.thead(
      <.tr(
        <.th("#"),
        <.th("Table heading"),
        <.th("Table heading"),
        <.th("Table heading"),
        <.th("Table heading")
      )
    )
  val tableBody =
    <.tbody(
      <.tr(
        <.th("PA"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell")
      ),
      <.tr(
        <.th("PE"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell")
      ),
      <.tr(
        <.th("PI"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell"),
        <.th("Table cell")
      )
    )

  val table = Table(Table.Props(striped = true, condensed = true, hover = true, responsive = true, fill = Some(true)), tableHead, tableBody)

  val panel = Panel(
    Panel.Props(
      header = Some("a header!"),
      onExit = Some(Callback{println("exited!!!")}),
      onSelect = Some(Callback{println("selected!")}),
      collapsible = Some(true)
    ),
    table)

  val listGroupItems = List("Item 1", "Item 2", "Item 3")
    .map{
      item => ListGroupItem(ListGroupItem.Props(), item).vdomElement
    }
  val listGroup = ListGroup(ListGroup.Props(fill = Some(true)), listGroupItems:_ *)

  val panelFilled = Panel(
    Panel.Props(header = Some("a header!"), collapsible = Some(true), defaultExpanded = true),
    "Some default panel content here.", listGroup)


  val listMap = ListMap (
    "A" -> List("Antonio", "Ana"),
    "B" -> List("Beatriz", "Belén", "Bernardo", "Bachata", "Boina"),
    "C" -> List("Cassandra", "Cecilia", "Carmen"),
    "D"-> List("Demetrio", "Diana", "Daniel"),
    "E"-> List("Esmeralda", "Esperanza")
  )

    val alphabeticPanels = listMap.keySet.toList.sortWith(_ < _).map{
      keyLetter =>
        val listItems = listMap(keyLetter).map(ListGroupItem(ListGroupItem.Props(onClick = Some(Callback{println("lul")})), _).vdomElement)
        val listGroup = ListGroup(ListGroup.Props(fill = Some(true)), listItems:_ *)
        Panel(
          Panel.Props(
            header = Some(keyLetter),
            bsStyle = BsStyle.primary,
            collapsible = Some(true)
          ),
          listGroup)
    }
  // Configure the router
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location].buildConfig { dsl =>
      import dsl._

    val userWrapper = SPACircuit.connect(_.users)

    (staticRoute(root, DashboardLocation) ~>
      renderR(
        ctl =>
          <.div(
            <.div("navBar???"),
            <.div(^.className := "so-padded", navBar),
            <.div("Simple Bootstrap button"),
            <.div(^.className := "so-padded", bootstrapButton),
            <.div("Bootstrap button with state and callbacks"),
            <.div(^.className := "so-padded", ButtonLoading(ButtonLoading.Props("button loading", Callback {println("Button loading callback")}))),
            <.div("Bootstrap button groups"),
            <.div(^.className := "so-padded", groupedButtons),
            <.div("Bootstrap table without borders and reactive"),
            <.div(^.className := "so-padded", table),
            <.div("Bootstrap panel"),
            <.div(^.className := "so-padded", panel),
            <.div("Bootstrap listGroup"),
            <.div(^.className := "so-padded", listGroup),
            <.div("Bootstrap panel filled with a listGroup (hope so)"),
            <.div(^.className := "so-padded", panelFilled),
            alphabeticPanels.toTagMod(panel => <.div(^.className := "so-padded", panel))
          )
      )
      | staticRoute("#users", UserLocation) ~> renderR(ctl => userWrapper(UserModule(_)).vdomElement)
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
