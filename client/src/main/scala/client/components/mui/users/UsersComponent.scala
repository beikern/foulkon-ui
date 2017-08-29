package client.components.mui.users

import client.appstate.{ObtainUserGroupFromExternalId, RefreshUsers, Users}
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.UserDetail

object UsersComponent {

  case class Props(proxy: ModelProxy[Pot[Users]])
  case class State(selectedUser: Option[UserDetail] = None)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshUsers))

    def render(p: Props, s: State) = {
      <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(usersFromProxy => UserList(usersFromProxy.users, id => p.proxy.dispatchCB(ObtainUserGroupFromExternalId(id))))
      )
    }
  }

  val component = ScalaComponent.builder[Props]("Users")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Users]]) = component(Props(proxy))

}
