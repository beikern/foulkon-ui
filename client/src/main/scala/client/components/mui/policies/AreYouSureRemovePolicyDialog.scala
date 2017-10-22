package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, TouchTapEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.PolicyDetail
import shared.requests.policies.{DeletePolicyPathParams, DeletePolicyRequest}

import scala.scalajs.js

object AreYouSureRemovePolicyDialog {

  case class Props(
      policyDetail: PolicyDetail,
      dialogOpened: Boolean,
      callbackToDelete: (DeletePolicyRequest) => Callback,
      changeDialogState: Boolean => Callback
  )

  class Backend($ : BackendScope[Props, Unit]) {

    def render(p: Props) = {

      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        Callback.info("Cancel clicked") >> p.changeDialogState(false)
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        val request = DeletePolicyRequest(DeletePolicyPathParams(p.policyDetail.org, p.policyDetail.name))
        Callback.info("Submit clicked") >> p.callbackToDelete(request) >> p.changeDialogState(false)
      }

      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "Cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "Delete", secondary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Removing ${p.policyDetail.name}, org ${p.policyDetail.org} , Are you sure?"),
        actions = actions,
        open = p.dialogOpened
      )()
    }
  }

  val component = ScalaComponent
    .builder[Props]("AreYouSureDeletePolicyDialog")
    .renderBackend[Backend]
    .build

  def apply(
      policyDetail: PolicyDetail,
      dialogOpened: Boolean,
      callbackToDelete: (DeletePolicyRequest) => Callback,
      changeDialogState: Boolean => Callback
  ) = component(Props(policyDetail, dialogOpened, callbackToDelete, changeDialogState))

}
