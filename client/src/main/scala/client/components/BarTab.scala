package client.components

import chandu0101.scalajs.react.components.materialui._
import japgolly.scalajs.react._
import japgolly.scalajs.react.raw._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object BarTab {

  case class Backend($ : BackendScope[Unit, Int]) {
    val onChange: (Int, ReactEventFromHtml, ReactElement) => Callback =
      (chosen, _, _) ⇒ $.setState(chosen) >> Callback.info(s"chose $chosen")

    def render(current: Int) =
      <.div(
          MuiTabs[Int](value = current, onChange = onChange)(
            MuiTab[Int](label = js.defined("Tab1"), value = 1)(
              "Tab1 Content"
            ),
            MuiTab[Int](label = js.defined("Tab2"), value = 2)(
              "Tab2 Content"
            )
          )
      )
  }

  val component = ScalaComponent
    .builder[Unit]("MuiTabsDemo")
    .initialState(2)
    .renderBackend[Backend]
    .build

  // EXAMPLE:END

  def apply() = component()

}
