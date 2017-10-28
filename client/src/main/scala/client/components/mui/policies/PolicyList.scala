package client.components.mui.policies

import client.components.others.ReactInfinite
import client.routes.AppRouter.Location
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.{Offset, Total}
import shared.entities.PolicyDetail
import shared.requests.policies._

import scala.scalajs.js

object PolicyList {

  case class Props(
      router: RouterCtl[Location],
      policies: List[PolicyDetail],
      offset: Offset,
      total: Total,
      deletePolicyCallback: (DeletePolicyRequest) => Callback,
      updatePolicyCallback: (UpdatePolicyRequest) => Callback,
      retrieveNextPoliciesCallback: (ReadPoliciesRequest) => Callback
  )
  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      println("render executed")
      val handleInfiniteLoad: Callback = {
        println("called handleInfiniteLoad")

        val request = ReadPoliciesRequest(p.offset)
        Callback.unless(p.offset >= p.total)(p.retrieveNextPoliciesCallback(request))
      }

      <.div(^.style:= scala.scalajs.js.Dynamic.literal("overflowAnchor" -> "none"), //https://github.com/utatti/perfect-scrollbar/issues/612 react-infinite has the same problem using Chrome.
        ReactInfinite(elementHeight = 300,
          onInfiniteLoad = js.defined(handleInfiniteLoad),
          infiniteLoadBeginEdgeOffset = js.defined(5),
          timeScrollStateLastsForAfterUserScrolls = js.defined(1000))(
          p.policies.map { policyDetail =>
            <.div(^.className := "card-nested-padded",
              PolicyCard(
                p.router,
                policyDetail,
                p.deletePolicyCallback,
                p.updatePolicyCallback
              )):VdomElement
          }
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("PoliciesList")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
    router: RouterCtl[Location],
    policies: List[PolicyDetail],
    offset: Offset,
    total: Total,
    deletePolicyCallback: (DeletePolicyRequest) => Callback,
    updatePolicyCallback: (UpdatePolicyRequest) => Callback,
    retrieveNextPoliciesCallback: (ReadPoliciesRequest) => Callback
  ) = component(
    Props(
      router,
      policies,
      offset,
      total,
      deletePolicyCallback,
      updatePolicyCallback,
      retrieveNextPoliciesCallback
    )
  )
}
