package client
package components.mui.groups

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiFloatingActionButton}
import client.appstate._
import client.appstate.groups._
import client.routes.AppRouter.Location
import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object GroupsComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createGroupButton = style(
      float.right
    )
  }

  case class Props(proxy: ModelProxy[Pot[Groups]], router: RouterCtl[Location])
  case class State(createGroupDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupDialogOpened = dialogState))
    }

    def showCreateGroupDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshGroups))

    def render(p: Props, s: State) = {
      <.div(
        CreateGroupDialog(
          s.createGroupDialogOpened,
          List(), // TODO beikern: MOCK
          changeCreateGroupDialogStateCallback,
          (org, name, path) => p.proxy.dispatchCB(CreateGroup(org, name, path))
        ),
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy()
          .render(
            groupsFromProxy =>
              GroupList(
                groupsFromProxy.groups,
                p.router,
                (groupOrg, actualGroupName, updatedGroupName, updatedGroupPath) =>
                  p.proxy.dispatchCB(UpdateGroup(groupOrg, actualGroupName, updatedGroupName, updatedGroupPath)),
                (groupOrg, groupName) => p.proxy.dispatchCB(DeleteGroup(groupOrg, groupName)),
                (id, groupOrg, groupName) => p.proxy.dispatchCB(RetrieveGroupMemberInfo(id, groupOrg, groupName)),
                (id, request) => p.proxy.dispatchCB(RetrieveGroupPolicyInfo(id, request))
            )
          ),
        <.div(
          Style.createGroupButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateGroupDialogCallback _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("Groups")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Groups]], router: RouterCtl[Location]) = component(Props(proxy, router))

}
