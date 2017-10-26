package client.components.mui.policies

import chandu0101.scalajs.react.components.ReactInfinite
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail
import shared.requests.policies.{DeletePolicyRequest, ReadPoliciesRequest, UpdatePolicyRequest}

import scala.scalajs.js

object PolicyList {

  case class Props(
      policies: List[PolicyDetail],
      deletePolicyCallback: (DeletePolicyRequest) => Callback,
      updatePolicyCallback: (UpdatePolicyRequest) => Callback,
      retrieveNextPoliciesCallback: (ReadPoliciesRequest) => Callback
  )
  case class State(
    offset: Int = 1
  )

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      val handleInfiniteLoad: Callback = {
        val request = ReadPoliciesRequest(s.offset)
        p.retrieveNextPoliciesCallback(request) >> $.modState(s => s.copy(offset = s.offset + 1))
      }

      <.div(
        ReactInfinite(elementHeight = 280,
          containerHeight = 300, onInfiniteLoad = js.defined(handleInfiniteLoad), infiniteLoadBeginBottomOffset = js.defined(10))(
          p.policies.map { policyDetail =>
            <.div(^.className := "card-nested-padded",
              PolicyCard(
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
    policies: List[PolicyDetail],
    deletePolicyCallback: (DeletePolicyRequest) => Callback,
    updatePolicyCallback: (UpdatePolicyRequest) => Callback,
    retrieveNextPoliciesCallback: (ReadPoliciesRequest) => Callback
  ) = component(
    Props(
      policies,
      deletePolicyCallback,
      updatePolicyCallback,
      retrieveNextPoliciesCallback
    )
  )
}
