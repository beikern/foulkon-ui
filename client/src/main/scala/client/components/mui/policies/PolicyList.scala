package client.components.mui.policies

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail

object PolicyList {

  case class Props(
    policies: List[PolicyDetail]
  )

  private val component = ScalaComponent
    .builder[Props]("PolicyList")
    .render_P(
      p => {
        <.div(
            p.policies
              .map{
                policyDetail =>
                  <.div(^.className := "card-nested-padded",
                        PolicyCard(
                          policyDetail
                        )
                  )
              }
              .toTagMod
        )
      }
    )
    .build

  def apply(
             policies: List[PolicyDetail]
           ) = component(
    Props(
      policies: List[PolicyDetail]
    )
  )
}
