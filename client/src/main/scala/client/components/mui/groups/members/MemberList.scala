package client
package components.mui.groups.members

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.groups.members.MemberAssociatedToGroupInfo

object MemberList {

  case class Props(
      id: String,
      organizationId: String,
      groupName: String,
      members: List[MemberAssociatedToGroupInfo],
      removeMemberCallback: (String, GroupOrg, GroupName, UserId) => Callback
  )

  private val component = ScalaComponent
    .builder[Props]("MemberList")
    .render_P(
      p => {
        <.div(
          p.members.map { member =>
            <.div(^.className := "card-nested-padded",
                  MemberCard(
                    p.id,
                    p.organizationId,
                    p.groupName,
                    member,
                    p.removeMemberCallback
                  ))
          }.toTagMod
        )
      }
    )
    .build

  def apply(
      id: String,
      organizationId: String,
      groupName: String,
      members: List[MemberAssociatedToGroupInfo],
      removeMemberCallback: (String, GroupOrg, GroupName, UserId) => Callback
  ) = component(
    Props(
      id,
      organizationId,
      groupName,
      members,
      removeMemberCallback
    )
  )
}
