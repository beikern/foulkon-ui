package client
package components.mui.groups

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiGridList, MuiIconButton}
import japgolly.scalajs.react._
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
    updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroup: (GroupOrg, GroupName) => Callback
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

  def apply(groupDetail: GroupDetail, updateGroup: (GroupOrg, GroupName, GroupName, GroupPath) => Callback, deleteGroup: (GroupOrg, GroupName) => Callback) =
    component(Props(groupDetail, updateGroup, deleteGroup))
}
