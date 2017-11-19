package client.components.mui
package users

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate.users._
import client.components.others.{ReactPaginate, ReactPaginatePage}
import client.routes.AppRouter.Location
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.requests.users.ReadUsersRequest
import shared.utils.constants._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object UsersComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createUserButton = style(
      float.right
    )
  }

  case class Props(proxy: ModelProxy[UserComponentZoomedModel], router: RouterCtl[Location])
  case class State(createUserDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateUserDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createUserDialogOpened = dialogState))
    }

    def showCreateUserDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createUserDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().users.isEmpty)(props.proxy.dispatchCB(FetchUsersToReset))

    def render(p: Props, s: State) = {
      def handlePageClick(page: ReactPaginatePage) = {
        val request = ReadUsersRequest(offset = page.selected * PageSize)
        p.proxy.dispatchCB(FetchUsers(request)) >> p.proxy.dispatchCB(UpdateSelectedPage(page.selected))
      }
      <.div(
        CreateUserDialog(
          s.createUserDialogOpened,
          changeCreateUserDialogStateCallback,
          (externalId, path) => p.proxy.dispatchCB(CreateUser(externalId, path))
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
        p.proxy().users.renderFailed(ex => "Error loading"),
        p.proxy().users.renderPending(_ > 500, _ => "Loading..."),
        p.proxy().users.renderEmpty(
            <.div(
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Users")).render
                )(),
                MuiCardText()(<.div("There's no users defined. Sorry! If you want to add one click the + button."))
              )
            )
          ),
        p.proxy().users
          .render(
            usersFromProxy =>
              usersFromProxy.users match {
                case Right(List()) =>
                  <.div(
                    ^.className := "card-padded",
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Users")).render
                      )(),
                      MuiCardText()(<.div("There's no users defined. Sorry! If you want to add one click the + button."))
                    )
                  )
                case Right(userDetails) =>
                  <.div(
                    ^.className := "card-padded",
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Users")).render
                      )(),
                      UserList(
                        p.router,
                        userDetails,
                        id => p.proxy.dispatchCB(DeleteUser(id))
                      )
                    )
                  )
                case Left(foulkonError) =>
                  <.div(
                    ^.className := "card-padded",
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Users")).render
                      )(),
                      MuiCardText()(<.div(
                        s"Can't show users because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
                    )
                  )
            }
          ),
        <.div(
          Style.createUserButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateUserDialog _))(
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

  def apply(proxy: ModelProxy[UserComponentZoomedModel], router: RouterCtl[Location]) = component(Props(proxy, router))

}