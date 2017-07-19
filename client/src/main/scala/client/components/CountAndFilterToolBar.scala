package client.components

import chandu0101.scalajs.react.components.materialui.{MuiCardText, MuiToolbar, MuiToolbarGroup}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

object CountAndFilterToolBar {

  case class Props(entityToCount: String)
  // create the React component for Dashboard
  private val component = ScalaComponent
    .builder[String]("CountAndFilterToolbar")
    .render_P(
      props =>
        MuiToolbar()(
          MuiToolbarGroup(firstChild = true)(
            MuiCardText()(<.div("hello"))
          )
        )
    )
    .build

  def apply(a: String) = component(a)

}
