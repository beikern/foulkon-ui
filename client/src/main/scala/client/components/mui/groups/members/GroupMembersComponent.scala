package client.components.mui.groups.members

import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.routes.AppRouter.Location
import diode.react._
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import client.appstate.groups.members._
import client.appstate.users.groups.UpdateSelectedPage
import client.components.others.{ReactPaginate, ReactPaginatePage}
import shared.requests.groups.members.{MemberGroupRequest, MemberGroupRequestPathParams}
import shared.utils.constants._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object GroupMembersComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createGroupMemberButton = style(
      float.right
    )
  }

  case class Props(
      organizationId: String,
      groupName: String,
      proxy: ModelProxy[GroupMemberComponentZoomedModel],
      router: RouterCtl[Location]
  )
  case class State(createGroupMemberAssocDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {

    val changeCreateGroupMemberAssocDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupMemberAssocDialogOpened = dialogState))
    }

    def showCreateGroupMemberDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupMemberAssocDialogOpened = true))
    }

    def mounted(props: Props) =
      props.proxy.dispatchCB(ResetGroupMembers) >> props.proxy.dispatchCB(FetchGroupMembers(MemberGroupRequest(MemberGroupRequestPathParams(props.organizationId, props.groupName), offset = 0)))

    def render(p: Props, s: State) = {
      def handlePageClick(page: ReactPaginatePage) = {
        val request = MemberGroupRequest(MemberGroupRequestPathParams(p.organizationId, p.groupName), offset = page.selected * PageSize)
        p.proxy.dispatchCB(FetchGroupMembers(request)) >> p.proxy.dispatchCB(UpdateSelectedPage(page.selected))
      }
      <.div(
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
        CreateMemberGroupDialog(
          p.organizationId,
          p.groupName,
          s.createGroupMemberAssocDialogOpened,
          changeCreateGroupMemberAssocDialogStateCallback,
          (org, name, userid) => p.proxy.dispatchCB(AddGroupMember(org, name, userid))
        ),
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Group ${p.groupName} members")).render
          )(),
          p.proxy().groupMembers.renderFailed(ex => "Error loading"),
          p.proxy().groupMembers.renderPending(_ > 500, _ => "Loading..."),
          p.proxy().groupMembers.renderEmpty(
            MuiCardText()(<.div("There's no members associated to this group, sorry!"))
          )
        ,
        p.proxy()
          .groupMembers
          .render(
            groupMembers =>
              groupMembers.groupMembers match {
                case Right(List()) =>
                  MuiCardText()(<.div("There's no members associated to this group, sorry!"))
                case Right(groupMembersToRender) =>
                  <.div(
                    ^.className := "card-nested-padded",
                    MemberList(
                      p.organizationId,
                      p.groupName,
                      groupMembersToRender,
                      (org, name, userid) => p.proxy.dispatchCB(RemoveGroupMember(org, name, userid)))
                  )
                case Left(foulkonError) =>
                  MuiCardText()(<.div(
                    s"Can't show group members because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
              }
          )
        ),
        <.div(
          Style.createGroupMemberButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateGroupMemberDialogCallback _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupMembersComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(
             organizationId: String,
             groupName: String,
             proxy: ModelProxy[GroupMemberComponentZoomedModel],
             router: RouterCtl[Location]
  ) = component(
    Props(
      organizationId,
      groupName,
      proxy,
      router
    )
  )
}
