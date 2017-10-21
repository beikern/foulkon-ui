package client
package components.mui.groups.members

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.groups.MemberInfo

object MemberList {

  case class Props(
      id: String,
      organizationId: String,
      groupName: String,
      members: List[MemberInfo],
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
      members: List[MemberInfo],
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
