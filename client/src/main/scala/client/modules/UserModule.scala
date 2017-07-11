package client.modules


import client.appstate.{RefreshUsers, Users}
import client.components.bootstrap.pagelayouts.panels.Panel
import client.components.own.UserList
import diode.data._
import diode.react._
import diode.react.ReactPot._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.component.Scala.BackendScope

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.VdomElement


object UserModule {

  case class Props(proxy: ModelProxy[Pot[Users]])

  class Backend(bs: BackendScope[Props, Unit]) {

    def mounted(props: Props) = {
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshUsers))
    }

    def render(p:Props): VdomElement = {
      <.div(
        p.proxy().renderFailed(ex => <.div("error loading")),
        p.proxy().renderPending(i => i > 500, i => <.div("Loading...")),
        p.proxy().render( users =>
          Panel(Panel.Props(), UserList(users.users).vdomElement)
        )
      )
    }
  }

  private val UserModule = {
    japgolly.scalajs.react.ScalaComponent.builder[Props]("UserModule")
      .renderBackend[Backend]
      .componentDidMount(scope => scope.backend.mounted(scope.props))
      .build
  }


  def apply(proxy: ModelProxy[Pot[Users]]) = UserModule(Props(proxy))


}
