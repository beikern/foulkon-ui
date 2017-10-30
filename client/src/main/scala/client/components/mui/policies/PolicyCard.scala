package client.components.mui.policies

import java.util.UUID

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
import client.routes.AppRouter.{Location, PolicyStatementsLocation}
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail
import shared.requests.policies.{DeletePolicyRequest, UpdatePolicyRequest}

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
      router: RouterCtl[Location],
      policyDetail: PolicyDetail,
      deletePolicyCallback: (DeletePolicyRequest) => Callback,
      updatePolicyCallback: (UpdatePolicyRequest) => Callback
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

    def showUpdatePolicyDialog(event: ReactEvent): Callback = {
      $.modState(s => s.copy(updateDialogOpened = true))
    }

    def render(p: Props, s: State) = {
      <.div(
        AreYouSureRemovePolicyDialog(
          p.policyDetail,
          s.deleteDialogOpened,
          p.deletePolicyCallback,
          changeDeletePolicyStateCallback
        ),
        UpdatePolicyDialog(
          p.policyDetail,
          s.updateDialogOpened,
          changeUpdatePolicyStateCallback,
          p.updatePolicyCallback
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
                  onClick = js.defined(showUpdatePolicyDialog _)
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
              href = js.defined(p.router.urlFor(PolicyStatementsLocation(UUID.fromString(p.policyDetail.id))).value)
            )()
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
      router: RouterCtl[Location],
      policyDetail: PolicyDetail,
      deletePolicyCallback: (DeletePolicyRequest) => Callback,
      updatePolicyCallback: (UpdatePolicyRequest) => Callback
  ) =
    component(
      Props(
        router,
        policyDetail,
        deletePolicyCallback,
        updatePolicyCallback
      )
    )
}
