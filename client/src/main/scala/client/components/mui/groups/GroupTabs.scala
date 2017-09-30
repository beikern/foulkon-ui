package client.components.mui.groups

import chandu0101.scalajs.react.components.materialui.{MuiTab, MuiTabs}
import client.appstate.GroupModule
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object GroupTabs {

  case class Props(proxy: ModelProxy[GroupModule])
  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      <.div(
        MuiTabs()(
          MuiTab(label = js.defined("groups crud"))(
            GroupsComponent(p.proxy.zoom(_.groups))
          ),
          MuiTab(label = js.defined("group members ops"))(),
          MuiTab(label = js.defined("group policies ops"))()
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupTabs")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(proxy: ModelProxy[GroupModule]) = component(Props(proxy))

}
