package client
package components.mui.groups

import java.util.UUID

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardActions, MuiCardHeader, MuiCardText, MuiDivider, MuiFlatButton, MuiGridList, MuiIconButton}
import client.routes.AppRouter.{GroupMembersLocation, Location}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.GroupDetail

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object GroupCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
    groupDetail: GroupDetail,
    router: RouterCtl[Location],
    updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroup: (GroupOrg, GroupName) => Callback,
    retrieveGroupMemberInfoCallback: (String, GroupOrg, GroupName) => Callback
  )

  case class State(
      deleteDialogOpened: Boolean = false,
      updateDialogOpened: Boolean = false
  )

  class Backend($ : BackendScope[Props, State]) {
    val changeDeleteGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }
    val changeUpdateGroupDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(updateDialogOpened = dialogState))
    }

    def showAreYouSureDeleteDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

    def showUpdateGroupDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(updateDialogOpened = true))
    }

    def render(p: Props, s: State) = {

      <.div(
        UpdateGroupDialog(
          p.groupDetail,
          s.updateDialogOpened,
          p.updateGroupCallback,
          changeUpdateGroupDialogStateCallback
        )(),
        AreYouSureRemoveGroupDialog(
          p.groupDetail.org,
          p.groupDetail.name,
          s.deleteDialogOpened,
          p.deleteGroup,
          changeDeleteGroupDialogStateCallback
        )(),
        MuiCard()(
          <.div(
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"${p.groupDetail.id}")).render
              )(),
              <.div(
                Style.editDeleteButton,
                MuiIconButton(
                  onClick = js.defined(showUpdateGroupDialog _)
                )(
                  Mui.SvgIcons.EditorModeEdit.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
                ),
                MuiIconButton(
                  onClick = js.defined(showAreYouSureDeleteDialog _)
                )(
                  Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
                )
              )
            )),
          MuiCardText()(
            <.div(
              <.p(<.b("Name: "), s"${p.groupDetail.name}"),
              <.p(<.b("Path: "), s"${p.groupDetail.path}"),
              <.p(<.b("created at: "), s"${p.groupDetail.createAt}"),
              <.p(<.b("update at: "), s"${p.groupDetail.updateAt}"),
              <.p(<.b("urn: "), s"${p.groupDetail.org}"),
              <.p(<.b("org: "), s"${p.groupDetail.org}")
            )
          ),
          MuiDivider()(),
          MuiCardActions()(
            MuiFlatButton(
              primary = js.defined(true),
              label = js.defined("members"),
              href = js.defined(p.router.urlFor(GroupMembersLocation(UUID.fromString(p.groupDetail.id))).value),
              onClick = js.defined((_) => p.retrieveGroupMemberInfoCallback(p.groupDetail.id, p.groupDetail.org, p.groupDetail.name))
            )()
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
    groupDetail: GroupDetail,
    router: RouterCtl[Location],
    updateGroup: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroup: (GroupOrg, GroupName) => Callback,
    retrieveGroupMemberInfoCallback: (String, GroupOrg, GroupName) => Callback
  ) =
    component(
      Props(
        groupDetail,
        router,
        updateGroup,
        deleteGroup,
        retrieveGroupMemberInfoCallback
      )
    )
}
