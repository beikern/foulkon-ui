package client.components.mui.groups.policies

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo

object GroupPolicyList {

  case class Props(
      organizationId: String,
      groupName: String,
      policies: List[PoliciesAssociatedToGroupInfo]
  )

  private val component = ScalaComponent
    .builder[Props]("GroupPolicyList")
    .render_P(
      p => {
        <.div(
          p.policies.map { policy =>
            <.div(^.className := "card-nested-padded",
                  GroupPolicyCard(
                    p.organizationId,
                    p.groupName,
                    policy
                  ))
          }.toTagMod
        )
      }
    )
    .build

  def apply(
      organizationId: String,
      groupName: String,
      policies: List[PoliciesAssociatedToGroupInfo]
  ) = component(
    Props(
      organizationId,
      groupName,
      policies
    )
  )
}
