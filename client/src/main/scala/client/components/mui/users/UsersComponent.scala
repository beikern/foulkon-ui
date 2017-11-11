package client.components.mui
package users

import chandu0101.scalajs.react.components.materialui.{Mui, MuiFloatingActionButton}
import client.appstate._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import client.appstate.users.{CreateUser, DeleteUser, ObtainUserGroupFromExternalId, RefreshUsers}

object UsersComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createUserButton = style(
      float.right
    )
  }

  case class Props(proxy: ModelProxy[Pot[Users]])
  case class State(createUserDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateUserDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createUserDialogOpened = dialogState))
    }

    def showAreYouSureDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createUserDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshUsers))

    def render(p: Props, s: State) = {
      <.div(
        CreateUserDialog(
          s.createUserDialogOpened,
          changeCreateUserDialogStateCallback,
          (externalId, path) => p.proxy.dispatchCB(CreateUser(externalId, path))
        ),
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy()
          .render(
            usersFromProxy =>
              UserList(
                usersFromProxy.users,
                id => p.proxy.dispatchCB(ObtainUserGroupFromExternalId(id)),
                id => p.proxy.dispatchCB(DeleteUser(id))
            )
          ),
        <.div(
          Style.createUserButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showAreYouSureDialog _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("Users")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Users]]) = component(Props(proxy))

}
