package client.components.mui.policies.statements

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardHeader, MuiCardText}
import client.appstate.Policies
import client.routes.AppRouter.Location
import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.FoulkonError
import shared.responses.policies.Statement

object PolicyStatementsComponent {

  case class Props(id: String, proxy: ModelProxy[Pot[Policies]], router: RouterCtl[Location])
  case class State()

  class Backend($ : BackendScope[Props, State]) {

    def mounted(props: Props) =
      Callback.empty

    def render(p: Props, s: State) = {
      val statements: Pot[Either[FoulkonError, Option[List[Statement]]]] =
        p.proxy.value.map { policiesWrapper =>
          policiesWrapper.policies.map { policyMap =>
            policyMap.get(p.id).map(_.statements)
          }
        }
      <.div(
        statements.renderFailed(ex => "Error loading"),
        statements.renderPending(_ > 500, _ => "Loading..."),
        statements.renderEmpty(
          MuiCard()(
            MuiCardHeader(
              title = <.span(<.b(s"Policy ${p.id} statements")).render
            )(),
            MuiCardText()(<.div("This policy is not defined. Sorry!"))
          )
        ),
        statements.render {
          case Left(foulkonError) =>
            MuiCard()(
              MuiCardHeader(
                title = <.span(<.b(s"Policy ${p.id} statements")).render
              )(),
              MuiCardText()(<.div(
                s"Can't show statements because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
            )
          case Right(Some(List())) =>
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Policy ${p.id} statements")).render
                )(),
                MuiCardText()(<.div("This policy does not have statements. Sorry!"))
              )
            )
          case Right(None) =>
            MuiCard()(
              MuiCardHeader(
                title = <.span(<.b(s"Policy ${p.id} statements")).render
              )(),
              MuiCardText()(<.div(s"Can't find the policy ${p.id}, statements can't be showed."))
            )
          case Right(Some(statementList)) =>
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Policy ${p.id} statements")).render
                )(),
                PolicyStatementList(statementList)
              )
            )
        }
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("PolicyStatementsComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(id: String, proxy: ModelProxy[Pot[Policies]], router: RouterCtl[Location]) =
    component(Props(id, proxy, router))

}
