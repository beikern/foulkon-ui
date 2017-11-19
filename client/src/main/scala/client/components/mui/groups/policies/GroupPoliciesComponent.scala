package client.components.mui.groups.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate.groups.policies.{FetchGroupPolicies, GroupPoliciesZoomedModel, ResetGroupPolicies, UpdateSelectedPage}
import client.components.others.{ReactPaginate, ReactPaginatePage}
import client.routes.AppRouter.Location
import diode.react._
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.requests.groups.policies.{PoliciesAssociatedToGroupRequest, PoliciesAssociatedToGroupRequestPathParams}
import shared.utils.constants._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object GroupPoliciesComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createGroupPolicyButton = style(
      float.right
    )
  }

  case class Props(
    organizationId: String,
    groupName: String,
    proxy: ModelProxy[GroupPoliciesZoomedModel],
    router: RouterCtl[Location]
  )
  case class State(createGroupPolicyAssocDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {

    val changeCreateGroupPolicyAssocDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupPolicyAssocDialogOpened = dialogState))
    }

    def showCreateGroupPolicyDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupPolicyAssocDialogOpened = true))
    }

    def mounted(props: Props) =
      props.proxy.dispatchCB(ResetGroupPolicies) >> props.proxy.dispatchCB(FetchGroupPolicies(PoliciesAssociatedToGroupRequest(PoliciesAssociatedToGroupRequestPathParams(props.organizationId, props.groupName), offset = 0)))

    def render(p: Props, s: State) = {
      def handlePageClick(page: ReactPaginatePage) = {
        val request = PoliciesAssociatedToGroupRequest(PoliciesAssociatedToGroupRequestPathParams(p.organizationId, p.groupName), offset = page.selected * PageSize)
        p.proxy.dispatchCB(FetchGroupPolicies(request)) >> p.proxy.dispatchCB(UpdateSelectedPage(page.selected))
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
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Group ${p.groupName} members")).render
          )(),
          p.proxy().groupsPolicies.renderFailed(ex => "Error loading"),
          p.proxy().groupsPolicies.renderPending(_ > 500, _ => "Loading..."),
          p.proxy().groupsPolicies.renderEmpty(
            MuiCardText()(<.div("There's no members associated to this group, sorry!"))
          ),
          p.proxy()
            .groupsPolicies
            .render(
              groupPolicies =>
                groupPolicies.groupPolicies match {
                  case Right(List()) =>
                    MuiCardText()(<.div("There's no policies associated to this group, sorry!"))
                  case Right(groupPoliciesToRender) =>
                    <.div(
                      ^.className := "card-nested-padded",
                        GroupPolicyList(
                          p.organizationId,
                          p.groupName,
                          groupPoliciesToRender
                        )
                    )
                  case Left(foulkonError) =>
                    MuiCardText()(<.div(
                      s"Can't show group policies because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
                }
            )
        ),
        <.div(
          Style.createGroupPolicyButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateGroupPolicyDialogCallback _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupPoliciesComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(
     organizationId: String,
     groupName: String,
     proxy: ModelProxy[GroupPoliciesZoomedModel],
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
