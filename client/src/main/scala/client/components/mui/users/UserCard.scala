package client.components.mui
package users

import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardActions, MuiCardHeader, MuiCardText, MuiDivider, MuiFlatButton, MuiGridList, MuiIconButton}
import client.appstate.UserWithGroup
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object UserCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
    userData: UserWithGroup,
    updateGroup: String => Callback,
    deleteUser: String => Callback
  )

  case class State(
    groupExpanded: Boolean = false,
    deleteDialogOpened: Boolean = false
  )

  class Backend($: BackendScope[Props, State]) {
    val changeDeleteUserDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }

    def toggleGroup(updateGroup: Callback, groupExpanded: Boolean)(event: ReactEvent): Callback = {
      if(!groupExpanded){
        updateGroup >> $.modState(s => s.copy(groupExpanded = !s.groupExpanded))
      } else {
        $.modState(s => s.copy(groupExpanded = !s.groupExpanded))
      }
    }
    def showAreYouSureDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

    def render(p: Props, s:State) = {
      val groupToRender = p.userData.group match {
        case Some(groups) =>
          groups.map(
            group =>
              GroupCard(group):VdomNode
          )
        case None =>
          List(<.p(""): VdomNode)
      }

// THIS WORKS TOO, just remove the variadic conversion
//    val groupToRender = p.userData.group match {
//      case Some(groups) =>
//        groups.map(
//          group =>
//            GroupCard(group)
//        ).toVdomArray
//      case None =>
//        <.p(""): VdomNode

      <.div(
        AreYouSureDialog(
          p.userData.user.externalId,
          s.deleteDialogOpened,
          p.deleteUser,
          changeDeleteUserDialogStateCallback)()
        ,
        MuiCard(expanded = js.defined(s.groupExpanded))(
          <.div(MuiGridList(cellHeight = js.defined(50))(
            MuiCardHeader(
              title = <.span(<.b(s"${p.userData.user.externalId}")).render
            )(),
            <.div(Style.editDeleteButton,
              MuiIconButton()(
                Mui.SvgIcons.EditorModeEdit.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
              ),
              MuiIconButton(
                onClick = js.defined(showAreYouSureDialog _)
              )(
                Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
              )
            )
          )),
          MuiCardText()(
            <.div(
              <.p(<.b("Path: "), s"${p.userData.user.path}"),
              <.p(<.b("Created at: "),s"${p.userData.user.createdAt}"),
              <.p(<.b("Updated at: "), s"${p.userData.user.updatedAt}"),
              <.p(<.b("Urn: "), s"${p.userData.user.urn}")
            )
          ),
          MuiDivider()(),
          MuiCardActions()(
            MuiFlatButton(
              label = js.defined("GROUPS"),
              primary = js.defined(true),
              onClick = js.defined(toggleGroup(p.updateGroup(p.userData.user.externalId), s.groupExpanded) _)
            )()
          ),
          MuiCardText(expandable = js.defined(true))(
            MuiGridList(cols = js.defined(2), padding = js.defined(8))(
              groupToRender:_ *
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent.builder[Props]("UserCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(userData: UserWithGroup,
            updateGroup: String => Callback,
            deleteUser: String => Callback) = component(Props(userData, updateGroup, deleteUser))
}
