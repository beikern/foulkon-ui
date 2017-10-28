package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate._
import client.appstate.policies.{CreatePolicy, DeletePolicy, FetchPoliciesToConcat, UpdatePolicy}
import client.routes.AppRouter.Location
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.requests.policies.ReadPoliciesRequest

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object PoliciesComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createPolicyButton = style(
      margin(0.px),
      top(auto),
      bottom(40.px),
      right(10.%%),
      position.fixed
    )
  }

  case class Props(proxy: ModelProxy[PolicyModule], router: RouterCtl[Location])
  case class State(createPolicyDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createPolicyDialogOpened = dialogState))
    }

    def showCreatePolicyDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createPolicyDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().policies.isEmpty)(props.proxy.dispatchCB(FetchPoliciesToConcat(ReadPoliciesRequest())))

    def render(p: Props, s: State) = {
      <.div(
        p.proxy().policies.renderFailed(ex => "Error loading"),
        p.proxy().policies.renderPending(_ > 500, _ => "Loading..."),
        p.proxy()
          .policies
          .renderEmpty(
            <.div(
              ^.className := "card-padded",
              CreatePolicyDialog(
                s.createPolicyDialogOpened,
                changeCreateGroupDialogStateCallback,
                (request) => p.proxy.dispatchCB(CreatePolicy(request))
              ),
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Policies")).render
                )(),
                MuiCardText()(<.div("There's no policies defined. Sorry! If you want to add one click the + button."))
              )
            )
          ),
        p.proxy()
          .policies
          .render(
            policiesFromProxy => {
              policiesFromProxy.policies match {
                case Right((total, List())) =>
                  <.div(
                    ^.className := "card-padded",
                    CreatePolicyDialog(
                      s.createPolicyDialogOpened,
                      changeCreateGroupDialogStateCallback,
                      (request) => p.proxy.dispatchCB(CreatePolicy(request))
                    ),
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Policies")).render
                      )(),
                      MuiCardText()(<.div("There's no policies defined. Sorry! If you want to add one click the + button."))
                    )
                  )
                case Right((total, policyDetails)) =>
                  <.div(
                    ^.className := "card-padded",
                    CreatePolicyDialog(
                      s.createPolicyDialogOpened,
                      changeCreateGroupDialogStateCallback,
                      (request) => p.proxy.dispatchCB(CreatePolicy(request))
                    ),
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Policies")).render
                      )(),
                      PolicyList(
                        p.router,
                        policyDetails,
                        p.proxy().offset,
                        total,
                        (request) => p.proxy.dispatchCB(DeletePolicy(request)),
                        (request) => p.proxy.dispatchCB(UpdatePolicy(request)),
                        (request) => p.proxy.dispatchCB(FetchPoliciesToConcat(request))
                      )
                    )
                  )
                case Left(foulkonError) =>
                  <.div(
                    ^.className := "card-padded",
                    CreatePolicyDialog(
                      s.createPolicyDialogOpened,
                      changeCreateGroupDialogStateCallback,
                      (request) => p.proxy.dispatchCB(CreatePolicy(request))
                    ),
                    MuiCard()(
                      MuiCardHeader(
                        title = <.span(<.b(s"Policies")).render
                      )(),
                      MuiCardText()(<.div(
                        s"Can't show policies because the following foulkon error. Code: ${foulkonError.code}, message: ${foulkonError.message}. Sorry!"))
                    )
                  )
              }
            }
          ),
        <.div(
          Style.createPolicyButton,
          MuiFloatingActionButton(
            onTouchTap = js.defined(showCreatePolicyDialogCallback _),
            mini = true
          )(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("Policies")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[PolicyModule], router: RouterCtl[Location]) = component(Props(proxy, router))

}
