package client.components.mui.groups

import chandu0101.scalajs.react.components.materialui.{MuiTab, MuiTabs}
import client.appstate.GroupModule
import client.routes.AppRouter.Location
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object GroupTabs {

  case class Props(proxy: ModelProxy[GroupModule], router: RouterCtl[Location])
  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      <.div(
        MuiTabs()(
          MuiTab(label = js.defined("groups crud"))(
            GroupsComponent(p.proxy.zoom(_.groups), p.router)
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

  def apply(proxy: ModelProxy[GroupModule], router: RouterCtl[Location]) = component(Props(proxy, router))

}
