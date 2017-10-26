package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate._
import client.routes.AppRouter.Location
import diode.data.Pot
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
      float.right
    )
  }

  case class Props(proxy: ModelProxy[Pot[Policies]], router: RouterCtl[Location])
  case class State(createPolicyDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {
    val changeCreateGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createPolicyDialogOpened = dialogState))
    }

    def showCreatePolicyDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createPolicyDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshPolicies(ReadPoliciesRequest(0,1))))

    def render(p: Props, s: State) = {
      <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy()
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
          .render(
            policiesFromProxy => {
              policiesFromProxy.policies match {
                case Right(List()) =>
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
                case Right(policyDetails) =>
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
                        policyDetails,
                        (request) => p.proxy.dispatchCB(DeletePolicy(request)),
                        (request) => p.proxy.dispatchCB(UpdatePolicy(request)),
                        (request) => p.proxy.dispatchCB(RefreshPolicies(request))
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
          MuiFloatingActionButton(onTouchTap = js.defined(showCreatePolicyDialogCallback _))(
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

  def apply(proxy: ModelProxy[Pot[Policies]], router: RouterCtl[Location]) = component(Props(proxy, router))

}
