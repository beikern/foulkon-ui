package client.components.mui
package users

import java.util.UUID

import chandu0101.scalajs.react.components.materialui.{
  Mui,
  MuiCard,
  MuiCardActions,
  MuiCardHeader,
  MuiCardText,
  MuiDivider,
  MuiFlatButton,
  MuiGridList,
  MuiIconButton
}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import client.routes.AppRouter.{Location, UserGroupsLocation}
import japgolly.scalajs.react.extra.router.RouterCtl
import shared.entities.UserDetail
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
      router: RouterCtl[Location],
      userDetail: UserDetail,
      deleteUser: String => Callback
  )

  case class State(
      groupExpanded: Boolean = false,
      deleteDialogOpened: Boolean = false
  )

  class Backend($ : BackendScope[Props, State]) {
    val changeDeleteUserDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }

    def toggleGroup(updateGroup: Callback, groupExpanded: Boolean)(event: ReactEvent): Callback = {
      if (!groupExpanded) {
        updateGroup >> $.modState(s => s.copy(groupExpanded = !s.groupExpanded))
      } else {
        $.modState(s => s.copy(groupExpanded = !s.groupExpanded))
      }
    }
    def showAreYouSureDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

    def render(p: Props, s: State) = {
      <.div(
        AreYouSureDialog(p.userDetail.externalId, s.deleteDialogOpened, p.deleteUser, changeDeleteUserDialogStateCallback)(),
        MuiCard(expanded = js.defined(s.groupExpanded))(
          <.div(
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"${p.userDetail.externalId}")).render
              )(),
              <.div(
                Style.editDeleteButton,
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
              <.p(<.b("Path: "), s"${p.userDetail.path}"),
              <.p(<.b("Created at: "), s"${p.userDetail.createdAt}"),
              <.p(<.b("Updated at: "), s"${p.userDetail.updatedAt}"),
              <.p(<.b("Urn: "), s"${p.userDetail.urn}")
            )
          ),
          MuiDivider()(),
          MuiCardActions()(
            MuiFlatButton(
              primary = js.defined(true),
              label = js.defined("GROUPS"),
              href = js.defined(p.router.urlFor(UserGroupsLocation(UUID.fromString(p.userDetail.id), p.userDetail.externalId)).value)
            )()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("UserCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(router: RouterCtl[Location], userDetail: UserDetail, deleteUser: String => Callback) =
    component(Props(router, userDetail, deleteUser))
}
