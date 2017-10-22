package client.components.mui

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCardText, MuiIconButton, MuiIconMenu, MuiMenuItem, MuiToolbar, MuiToolbarGroup}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object CountAndFilterToolBar {

  case class Props(entityToCount: String, nEntities: Int)
  // create the React component for Dashboard
  private val component = ScalaComponent
    .builder[Props]("CountAndFilterToolbar")
    .render_P(
      props =>
        MuiToolbar()(
          MuiToolbarGroup(firstChild = true)(
            MuiCardText()(<.div(s"${props.entityToCount}: ${props.nEntities}"))
          ),
          MuiToolbarGroup(lastChild = true)(
            MuiIconMenu[String](
              desktop = true,
              iconButtonElement = MuiIconButton()(
                Mui.SvgIcons.ActionSearch.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
              )
            )(
              MuiMenuItem(value = "1", primaryText = js.defined("GitHub"))(),
              MuiMenuItem(value = "2", primaryText = js.defined("About"))()
            )
          )
      )
    )
    .build

  def apply(a: Props) = component(a)

}
