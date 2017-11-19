package client
package components.mui.groups

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate.groups._
import client.components.others.{ReactPaginate, ReactPaginatePage}
import client.routes.AppRouter.Location
import diode.react._
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.requests.groups.ReadGroupsRequest
import shared.utils.constants._

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

  case class Props(proxy: ModelProxy[GroupComponentZoomedModel], router: RouterCtl[Location])
  case class State(createGroupDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupDialogOpened = dialogState))
    }

    def showCreateGroupDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().groups.isEmpty)(props.proxy.dispatchCB(FetchGroupsToReset))

    def render(p: Props, s: State) = {
      def handlePageClick(page: ReactPaginatePage) = {
        val request = ReadGroupsRequest(offset = page.selected * PageSize)
        p.proxy.dispatchCB(FetchGroups(request)) >> p.proxy.dispatchCB(UpdateSelectedPage(page.selected))
      }

      <.div(
        CreateGroupDialog(
          s.createGroupDialogOpened,
          List(), // TODO beikern: MOCK
          changeCreateGroupDialogStateCallback,
          (org, name, path) => p.proxy.dispatchCB(CreateGroup(org, name, path))
        ),
        ReactPaginate(
          pageCount = p.proxy().totalPages,
          pageRangeDisplayed = 10,
          marginPagesDisplayed = 2,
          onPageChange = js.defined(handlePageClick _),
          containerClassName = js.defined("pagination"),
          activeClassName = js.defined("active"),
          breakClassName = js.defined("break-me"),
          breakLabel = js.defined(<.a("...")),
          forcePage = js.defined(p.proxy().selectedPage),
          disableInitialCallback = js.defined(false)
        )(),
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Groups")).render
          )(),
          p.proxy().groups.renderFailed(ex => "Error loading"),
          p.proxy().groups.renderPending(_ > 500, _ => "Loading..."),
          p.proxy().groups.renderEmpty(
            MuiCardText()(<.div("There's no groups defined. Sorry! If you want to add one click the + button."))
            ),
          p.proxy().groups.render(
            groupsFromProxy =>
              groupsFromProxy.groups match {
                case Right(List()) =>
                  MuiCardText()(<.div("There's no groups defined. Sorry! If you want to add one click the + button."))
                case Right(groupDetails) =>
                  GroupList(
                    groupDetails,
                    p.router,
                    (groupOrg, actualGroupName, updatedGroupName, updatedGroupPath) =>
                      p.proxy.dispatchCB(UpdateGroup(groupOrg, actualGroupName, updatedGroupName, updatedGroupPath)),
                    (groupOrg, groupName) => p.proxy.dispatchCB(DeleteGroup(groupOrg, groupName))
                  )
                case Left(foulkonError) =>
                  MuiCardText()(<.div(
                    s"Can't show user groups because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
              }
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

  def apply(proxy: ModelProxy[GroupComponentZoomedModel], router: RouterCtl[Location]) = component(Props(proxy, router))

}
