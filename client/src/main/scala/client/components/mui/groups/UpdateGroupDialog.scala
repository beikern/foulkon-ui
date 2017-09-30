package client
package components.mui.groups

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, MuiTextField, TouchTapEvent}
import client.components.utils.FoulkonMaxLengths._
import client.components.utils.FoulkonRegexPatterns._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}
import shared.entities.{GroupDetail, UpdateGroupData}

import scala.scalajs.js

object UpdateGroupDialog {

  case class Props(
      actualGroup: GroupDetail,
      dialogOpened: Boolean,
      updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
      changeDialogState: Boolean => Callback
  )
  case class State(
      inputValidated: Boolean = false,
      nameValidation: Boolean = false,
      nameErrorText: js.UndefOr[VdomNode] = js.undefined,
      nameValue: Option[String] = None,
      pathValidation: Boolean = false,
      pathErrorText: js.UndefOr[VdomNode] = js.undefined,
      pathValue: Option[String] = None
  )

  class Backend($ : BackendScope[Props, State]) {

    val nameValidationCallback: (ReactEventFromInput, String) => Callback = { (_, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                nameValidation = false,
                nameErrorText = js.defined("Input must be non empty."),
                nameValue = None,
                inputValidated = false
            ))
        case value if value.length > nameMaxLength =>
          $.modState(
            s =>
              s.copy(
                nameValidation = false,
                nameErrorText = js.defined(s"Input must have less than $nameMaxLength characters."),
                nameValue = None,
                inputValidated = false
            ))
        case value if !namePattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                nameValidation = false,
                nameValue = None,
                nameErrorText = js.defined(s"Input does not match the pattern $namePattern. Name example: group1")
            ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                nameValidation = true,
                nameErrorText = js.undefined,
                nameValue = Some(actualValue),
                inputValidated = s.pathValidation
            ))
      }
    }

    val pathValidationCallback: (ReactEventFromInput, String) => Callback = { (_, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                pathValidation = false,
                pathErrorText = js.defined("Input must be non empty."),
                pathValue = None,
                inputValidated = false
            ))
        case value if value.length > pathMaxLength =>
          $.modState(
            s =>
              s.copy(pathValidation = false,
                     pathErrorText = js.defined(s"Input must have less than $pathMaxLength characters."),
                     pathValue = None,
                     inputValidated = false))
        case value if !pathPattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                pathValidation = false,
                pathErrorText = js.defined(s"input does not match the pattern $pathPattern. Path example: /example/admin"),
                pathValue = None,
                inputValidated = false
            ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                pathValidation = true,
                pathErrorText = js.undefined,
                pathValue = Some(actualValue),
                inputValidated = s.nameValidation
            ))
      }
    }

    def render(p: Props, s: State) = {
      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        p.changeDialogState(false) >> $.setState(State())
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        val updateGroupData = for {
          nv <- s.nameValue
          pv <- s.pathValue
        } yield {
          UpdateGroupData(
            nv,
            pv
          )
        }

        updateGroupData match {
          case Some(groupDataToUpdate) =>
            p.changeDialogState(false) >>
              p.updateGroupCallback(
                p.actualGroup.org,
                p.actualGroup.name,
                groupDataToUpdate.name,
                groupDataToUpdate.path) >>
              $.setState(State())
          case None =>
            Callback.log(s"Something failed, the group was no updated, wooops!") >> p.changeDialogState(false) >> $.setState(State())
        }
      }
      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "update", disabled = js.defined(!s.inputValidated), secondary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Update group"),
        actions = actions,
        open = p.dialogOpened
      )(
        <.div(
          MuiTextField(
            hintText = js.defined("Name"),
            onChange = js.defined(nameValidationCallback),
            errorText = s.nameErrorText
          )()
        ),
        <.div(
          MuiTextField(
            hintText = js.defined("Path"),
            onChange = js.defined(pathValidationCallback),
            errorText = s.pathErrorText
          )()
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("CreateGroupDialog")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
     actualGroup: GroupDetail,
     dialogOpened: Boolean,
     updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
     changeDialogState: Boolean => Callback
  ) = component(
    Props(
      actualGroup,
      dialogOpened,
      updateGroupCallback,
      changeDialogState
    )
  )

}
