package client
package components.mui.groups.members

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, TouchTapEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object AreYouSureRemoveGroupMemberDialog {

  case class Props(
      groupOrg: String,
      groupName: String,
      userId: String,
      dialogOpened: Boolean,
      callbackToDelete: (GroupOrg, GroupName, UserId) => Callback,
      changeDialogState: Boolean => Callback
  )

  class Backend($ : BackendScope[Props, Unit]) {

    def render(p: Props) = {

      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        Callback.info("Cancel clicked") >> p.changeDialogState(false)
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        Callback.info("Submit clicked") >> p.callbackToDelete(p.groupOrg, p.groupName, p.userId) >> p.changeDialogState(false)
      }

      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "Cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "Remove", secondary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Removing ${p.userId} from org ${p.groupOrg} with name ${p.groupName}, Are you sure?"),
        actions = actions,
        open = p.dialogOpened
      )()
    }
  }

  val component = ScalaComponent
    .builder[Props]("AreYouSureRemoveMemberDialog")
    .renderBackend[Backend]
    .build

  def apply(
      groupOrg: String,
      groupName: String,
      userId: String,
      dialogOpened: Boolean,
      callbackToDelete: (GroupOrg, GroupName, UserId) => Callback,
      changeDialogState: Boolean => Callback
  ) = component(Props(groupOrg, groupName, userId, dialogOpened, callbackToDelete, changeDialogState))

}
