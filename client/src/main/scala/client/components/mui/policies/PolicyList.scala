package client.components.mui.policies

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail
import shared.requests.policies.{DeletePolicyRequest, UpdatePolicyRequest}

object PolicyList {

  case class Props(
      policies: List[PolicyDetail],
      deletePolicyCallback: (DeletePolicyRequest) => Callback,
      updatePolicyCallback: (UpdatePolicyRequest) => Callback
  )

  private val component = ScalaComponent
    .builder[Props]("PolicyList")
    .render_P(
      p => {
        <.div(
          p.policies.map { policyDetail =>
            <.div(^.className := "card-nested-padded",
                  PolicyCard(
                    policyDetail,
                    p.deletePolicyCallback,
                    p.updatePolicyCallback
                  ))
          }.toTagMod
        )
      }
    )
    .build

  def apply(
    policies: List[PolicyDetail],
    deletePolicyCallback: (DeletePolicyRequest) => Callback,
    updatePolicyCallback: (UpdatePolicyRequest) => Callback
  ) = component(
    Props(
      policies,
      deletePolicyCallback,
      updatePolicyCallback
    )
  )
}
