package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
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
import shared.entities.PolicyDetail
import shared.requests.policies.DeletePolicyRequest

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object PolicyCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
      policyDetail: PolicyDetail,
      deletePolicyCallback: (DeletePolicyRequest) => Callback
  )

  case class State(
      deleteDialogOpened: Boolean = false,
      updateDialogOpened: Boolean = false,
      statementExpanded: Boolean = false
  )

  class Backend($ : BackendScope[Props, State]) {
    val changeDeletePolicyStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(deleteDialogOpened = dialogState))
    }
    val changeUpdatePolicyStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(updateDialogOpened = dialogState))
    }

    def showAreYouSureDeleteDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(deleteDialogOpened = true))
    }

//    def showUpdateGroupDialog(event: ReactEvent): Callback = {
//      $.modState(s => s.copy(updateDialogOpened = true))
//    }

    def render(p: Props, s: State) = {

      def toggleStatement(event: ReactEvent): Callback = {
        $.modState(s => s.copy(statementExpanded = !s.statementExpanded))
      }

      val statementsToRender = p.policyDetail.statements.map { statement =>
        MuiCard()(
          MuiCardText()(
            <.p(<.b("effect: "), s"${statement.effect}"),
            <.p(<.b("actions: "), s"${statement.actions.mkString(", ")}"),
            <.p(<.b("resources: "), s"${statement.resources.mkString(", ")}")
          )
        ): VdomNode
      }

      <.div(
        AreYouSureRemovePolicyDialog(
          p.policyDetail,
          s.deleteDialogOpened,
          p.deletePolicyCallback,
          changeDeletePolicyStateCallback
        ),
        MuiCard(expanded = js.defined(s.statementExpanded))(
          <.div(
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"${p.policyDetail.id}")).render
              )(),
              <.div(
                Style.editDeleteButton,
                MuiIconButton(
//                  onClick = js.defined(showUpdateGroupDialog _)
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
              <.p(<.b("name: "), s"${p.policyDetail.name}"),
              <.p(<.b("path: "), s"${p.policyDetail.path}"),
              <.p(<.b("createAt: "), s"${p.policyDetail.createAt}"),
              <.p(<.b("updateAt: "), s"${p.policyDetail.updateAt}"),
              <.p(<.b("urn: "), s"${p.policyDetail.urn}"),
              <.p(<.b("org: "), s"${p.policyDetail.org}")
            )
          ),
          MuiDivider()(),
          MuiCardActions()(
            MuiFlatButton(
              label = js.defined("STATEMENTS"),
              primary = js.defined(true),
              onClick = js.defined(toggleStatement _)
            )()
          ),
          MuiCardText(expandable = js.defined(true))(
            MuiGridList(cols = js.defined(2), padding = js.defined(8))(
              statementsToRender: _*
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("PolicyCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      policyDetail: PolicyDetail,
      deletePolicyCallback: (DeletePolicyRequest) => Callback
  ) =
    component(
      Props(
        policyDetail,
        deletePolicyCallback
      )
    )
}
