package client.components

import chandu0101.scalajs.react.components.materialui.{MuiFlatButton, MuiIconButton, MuiIconMenu, MuiMenuItem, MuiSvgIcons, MuiToolbar, MuiToolbarGroup, MuiToolbarSeparator}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object NavToolBar {
  val component =
    ScalaComponent
      .builder[String]("NavToolBar")
  .render(
    p =>
      MuiToolbar()(
        MuiToolbarGroup(firstChild = true)(
          MuiFlatButton(key = "HomeButton", label = p.props)(),
          MuiFlatButton(key = "UsersButton", label = "USERS")(),
          MuiFlatButton(key = "GroupsButton", label = "GROUPS")(),
          MuiFlatButton(key = "PoliciesButton", label = "POLICIES")()
        ),
        MuiToolbarGroup(lastChild = true)(
          MuiToolbarSeparator()(),
          MuiIconMenu[String](
            desktop = true,
            iconButtonElement = MuiIconButton()(
              MuiSvgIcons.NavigationMoreVert.apply(
                style = js.Dynamic.literal(width = "30px", height = "30px")
              )()
            )
          )(
            MuiMenuItem(value = "1", primaryText = js.defined("GitHub"))(), // TODO beikern this elements don't not work at all :(
            MuiMenuItem(value = "2", primaryText = js.defined("About"))()
          )
        )
      )
  ).build

  def apply(name: String) = component(name)
}
