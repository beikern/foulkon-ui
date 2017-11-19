package client
package components.mui.groups.members

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, MuiTextField, TouchTapEvent}
import client.components.utils.FoulkonMaxLengths._
import client.components.utils.FoulkonRegexPatterns._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}

import scala.scalajs.js

object CreateMemberGroupDialog {

  case class Props(
      organizationId: String,
      groupName: String,
      dialogOpened: Boolean,
      changeDialogState: Boolean => Callback,
      createGroupMemberCallback: (GroupOrg, GroupName, UserId) => Callback
  )
  case class State(
      inputValidated: Boolean = false,
      userIdErrorText: js.UndefOr[VdomNode] = js.undefined,
      userIdValue: Option[String] = None
  )

  class Backend($ : BackendScope[Props, State]) {

    val userIdValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                userIdErrorText = js.defined("Input must be non empty."),
                userIdValue = None,
                inputValidated = false
            ))
        case value if value.length > nameMaxLength =>
          $.modState(
            s =>
              s.copy(
                userIdErrorText = js.defined(s"Input must have less than $nameMaxLength characters."),
                userIdValue = None,
                inputValidated = false
            ))
        case value if !namePattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                userIdValue = None,
                userIdErrorText = js.defined(s"Input does not match the pattern $namePattern. Name example: user"),
                inputValidated = false
            ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                userIdValue = Some(actualValue),
                inputValidated = true
            )
          )
      }
    }

    def render(p: Props, s: State) = {
      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        p.changeDialogState(false) >> $.setState(State())
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        s.userIdValue match {
          case Some(userIdValue) =>
            p.changeDialogState(false) >> p.createGroupMemberCallback(p.organizationId, p.groupName, userIdValue) >> $.setState(State())
          case None =>
            Callback.log(s"Something failed, the member was not associated, wooops!") >> p.changeDialogState(false) >> $.setState(State())
        }
      }
      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "create", disabled = js.defined(!s.inputValidated), primary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Add member to group"),
        actions = actions,
        open = p.dialogOpened
      )(
        <.div(
          MuiTextField(
            hintText = js.defined("user ID"),
            onChange = js.defined(userIdValidationCallback),
            errorText = s.userIdErrorText
          )()
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("CreateMemberGroupDialog")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      organizationId: String,
      groupName: String,
      dialogOpened: Boolean,
      changeDialogState: Boolean => Callback,
      createGroupMemberCallback: (GroupOrg, GroupName, UserId) => Callback
  ) = component(
    Props(organizationId, groupName, dialogOpened, changeDialogState, createGroupMemberCallback)
  )

}
