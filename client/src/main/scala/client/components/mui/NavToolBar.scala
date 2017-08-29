package client.components.mui

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiFlatButton, MuiIconButton, MuiIconMenu, MuiMenuItem, MuiToolbar, MuiToolbarGroup, MuiToolbarSeparator}
import client.routes.AppRouter.{GroupsLocation, Location, PoliciesLocation, UsersLocation}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object NavToolBar {

  case class Props(router: RouterCtl[Location], title: String)

  val component =
    ScalaComponent
      .builder[Props]("NavToolBar")
  .render_P(
    p =>
      MuiToolbar()(
        MuiToolbarGroup(firstChild = true)(
          MuiFlatButton(key = "HomeButton", label = p.title, href = js.defined(p.router.urlFor(UsersLocation).value))(),
          MuiFlatButton(key = "UsersButton", label = "USERS", href = js.defined(p.router.urlFor(UsersLocation).value))(),
          MuiFlatButton(key = "GroupsButton", label = "GROUPS", href = js.defined(p.router.urlFor(GroupsLocation).value))(),
          MuiFlatButton(key = "PoliciesButton", label = "POLICIES", href = js.defined(p.router.urlFor(PoliciesLocation).value))()
        ),
        MuiToolbarGroup(lastChild = true)(
          MuiToolbarSeparator()(),
          MuiIconMenu[String](
            desktop = true,
            iconButtonElement = MuiIconButton()(
              Mui.SvgIcons.NavigationMoreVert.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))())
          )(
            MuiMenuItem(value = "1", primaryText = js.defined("GitHub"))(),
            MuiMenuItem(value = "2", primaryText = js.defined("About"))()
          )
        )
      )
  ).build

  def apply(router: RouterCtl[Location], title: String) = component(Props(router, title))
}
