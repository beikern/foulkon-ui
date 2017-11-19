package client.components.mui.users.groups

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardHeader, MuiCardText}
import client.appstate.users.groups.{FetchUserGroups, ResetUserGroups, UpdateSelectedPage, UserGroupsComponentZoomedModel}
import client.components.others.{ReactPaginate, ReactPaginatePage}
import client.routes.AppRouter.Location
import diode.react._
import diode.react.ReactPot._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.requests.users.groups.ReadUserGroupsRequest
import shared.utils.constants._

import scala.scalajs.js

object UserGroupsComponent {

  case class Props(id: String, externalId: String, proxy: ModelProxy[UserGroupsComponentZoomedModel], router: RouterCtl[Location])
  case class State()

  class Backend($ : BackendScope[Props, State]) {

    def mounted(props: Props) =
      props.proxy.dispatchCB(ResetUserGroups) >> props.proxy.dispatchCB(FetchUserGroups(ReadUserGroupsRequest(props.externalId, offset = 0)))

    def render(p: Props, s: State) = {
      def handlePageClick(page: ReactPaginatePage) = {
        val request = ReadUserGroupsRequest(userExternalId = p.externalId, offset = page.selected * PageSize)
        p.proxy.dispatchCB(FetchUserGroups(request)) >> p.proxy.dispatchCB(UpdateSelectedPage(page.selected))
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
            title = <.span(<.b(s"User with id ${p.id} and externalId ${p.externalId}. Groups")).render
          )(),
          p.proxy().userGroups.renderFailed(ex => "Error loading"),
          p.proxy().userGroups.renderPending(_ > 500, _ => "Loading..."),
          p.proxy()
            .userGroups
            .renderEmpty(
              MuiCardText()(<.div("There's no groups associated to this user, sorry!"))
            ),
          p.proxy()
            .userGroups
            .render(
              userGroups =>
                userGroups.userGroups match {
                  case Right(List()) =>
                    MuiCardText()(<.div("There's no groups associated to this user, sorry!"))
                  case Right(userGroupsToRender) =>
                    <.div(
                      userGroupsToRender.map { ug =>
                        <.div(^.className := "card-nested-padded", UserGroupCard(ug)())
                      }.toTagMod
                    )
                  case Left(foulkonError) =>
                    MuiCardText()(<.div(
                      s"Can't show user groups because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
              }
            )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("UserGroupsComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(
      id: String,
      externalId: String,
      proxy: ModelProxy[UserGroupsComponentZoomedModel],
      router: RouterCtl[Location]
  ) = component(Props(id, externalId, proxy, router))

}
