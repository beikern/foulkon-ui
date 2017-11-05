package client.components.mui.policies

import client.routes.AppRouter.Location
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail
import shared.requests.policies.{DeletePolicyRequest, UpdatePolicyRequest}

object PolicyList {

  case class Props(
                    router: RouterCtl[Location],
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
                p.router,
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
             router: RouterCtl[Location],
             policies: List[PolicyDetail],
             deletePolicyCallback: (DeletePolicyRequest) => Callback,
             updatePolicyCallback: (UpdatePolicyRequest) => Callback
           ) = component(
    Props(
      router,
      policies,
      deletePolicyCallback,
      updatePolicyCallback
    )
  )
}
