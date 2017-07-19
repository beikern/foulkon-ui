package client.components

import chandu0101.scalajs.react.components.materialui._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object AppBar {
  val component = ScalaComponent
    .builder[Unit]("FoulkonAppBar")
    .render(
        P => 
          <.div(
            MuiAppBar(
              title = js.defined("Foulkon UI"),
              showMenuIconButton = false
            )()
        ))
    .build

  // EXAMPLE:END

  def apply() = component()
}
