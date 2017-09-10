package client.components.mui

import japgolly.scalajs.react._
import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, TouchTapEvent}
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object AreYouSureDialog {

  case class Props(
    entityIdToDelete: String,
    dialogOpened: Boolean,
    callbackToDelete: String => Callback,
    changeDialogState: Boolean => Callback
  )

  class Backend($: BackendScope[Props, Unit]) {

    def render(p: Props) = {

      def handleDialogCancel: TouchTapEvent => Callback = {
        TouchTapEvent => Callback.info("Cancel clicked") >> p.changeDialogState(false)
      }
      def handleDialogSubmit: TouchTapEvent => Callback = {
        TouchTapEvent => Callback.info("Submit clicked") >> p.callbackToDelete(p.entityIdToDelete) >> p.changeDialogState(false)
      }

      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1",
            label = "Cancel",
            onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2",
            label = "Delete",
            secondary = true,
            onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = s"Removing ${p.entityIdToDelete}, Are you sure?",
        actions = actions,
        open = p.dialogOpened
      )()
    }
  }

  val component = ScalaComponent
    .builder[Props]("AreYouSureDialog")
    .renderBackend[Backend]
    .build


  def apply(
    entityIdToDelete: String,
    dialogOpened: Boolean,
    callbackToDelete: String => Callback,
    changeDialogState: Boolean => Callback
  ) = component(Props(entityIdToDelete, dialogOpened, callbackToDelete, changeDialogState))

}
