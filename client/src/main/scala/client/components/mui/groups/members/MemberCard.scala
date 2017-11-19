package client
package components.mui.groups.members

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiGridList, MuiIconButton}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.groups.members.MemberAssociatedToGroupInfo

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object MemberCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
      organizationId: String,
      groupName: String,
      memberInfo: MemberAssociatedToGroupInfo,
      removeMemberCallback: (GroupOrg, GroupName, UserId) => Callback
  )

  case class State(
      deleteDialogOpened: Boolean = false
  )

  class Backend($ : BackendScope[Props, State]) {

    val changeRemoveGroupMemberDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }

    def showAreYouSureDeleteDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

    def render(p: Props, s: State) = {

      <.div(
        AreYouSureRemoveGroupMemberDialog(
          p.organizationId,
          p.groupName,
          p.memberInfo.user,
          s.deleteDialogOpened,
          p.removeMemberCallback,
          changeRemoveGroupMemberDialogStateCallback
        ),
        MuiCard()(
          <.div(
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"${p.memberInfo.user}")).render
              )(),
              <.div(
                Style.editDeleteButton,
                MuiIconButton(
                  onClick = js.defined(showAreYouSureDeleteDialog _)
                )(
                  Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
                )
              )
            )),
          MuiCardText()(
            <.div(
              <.p(<.b("Joined: "), s"${p.memberInfo.joined}")
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      organizationId: String,
      groupName: String,
      memberInfo: MemberAssociatedToGroupInfo,
      removeMemberCallback: (GroupOrg, GroupName, UserId) => Callback
  ) =
    component(
      Props(
        organizationId,
        groupName,
        memberInfo,
        removeMemberCallback
      )
    )
}
