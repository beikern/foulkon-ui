package client.components.mui.groups.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiGridList, MuiIconButton}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object GroupPolicyCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
      id: String,
      organizationId: String,
      groupName: String,
      policyInfo: PoliciesAssociatedToGroupInfo
  )

  case class State(
      deleteDialogOpened: Boolean = false
  )

  class Backend($ : BackendScope[Props, State]) {

    val changeRemoveGroupPolicyDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }

    def showAreYouSureDeleteDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

    def render(p: Props, s: State) = {

      <.div(
        MuiCard()(
          <.div(
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"${p.policyInfo.policy}")).render
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
              <.p(<.b("Attached: "), s"${p.policyInfo.attached}")
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupPolicyCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      id: String,
      organizationId: String,
      groupName: String,
      policyInfo: PoliciesAssociatedToGroupInfo
  ) =
    component(
      Props(
        id,
        organizationId,
        groupName,
        policyInfo
      )
    )
}
