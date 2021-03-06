package client
package components.mui.groups

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, MuiTextField, TouchTapEvent}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}
import shared.entities.CreateGroupData
import client.components.utils.FoulkonRegexPatterns._
import client.components.utils.FoulkonMaxLengths._

import scala.scalajs.js

object CreateGroupDialog {

  case class Props(
      dialogOpened: Boolean,
      organizations: List[String],
      changeDialogState: Boolean => Callback,
      createGroupCallback: (GroupOrg, GroupName, GroupPath) => Callback
  )
  case class State(
      inputValidated: Boolean = false,
      orgValidation: Boolean = false,
      orgErrorText: js.UndefOr[VdomNode] = js.undefined,
      orgValue: Option[String] = None,
      nameValidation: Boolean = false,
      nameErrorText: js.UndefOr[VdomNode] = js.undefined,
      nameValue: Option[String] = None,
      pathValidation: Boolean = false,
      pathErrorText: js.UndefOr[VdomNode] = js.undefined,
      pathValue: Option[String] = None
  )

  class Backend($ : BackendScope[Props, State]) {

    val orgValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgErrorText = js.defined("Input must be non empty."),
                orgValue = None,
                inputValidated = false
            ))
        case value if value.length > nameMaxLength =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgErrorText = js.defined(s"Input must have less than $orgMaxLength characters."),
                orgValue = None,
                inputValidated = false
            ))
        case value if !orgPattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgValue = None,
                orgErrorText = js.defined(s"Input does not match the pattern $orgPattern. Name example: group1")
            ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                orgValidation = true,
                orgErrorText = js.undefined,
                orgValue = Some(actualValue),
                inputValidated = s.pathValidation && s.nameValidation
            ))
      }
    }

    val nameValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
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
                inputValidated = s.pathValidation && s.orgValidation
            ))
      }
    }

    val pathValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
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
                inputValidated = s.nameValidation && s.orgValidation
            ))
      }
    }

    def render(p: Props, s: State) = {
      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        p.changeDialogState(false) >> $.setState(State())
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        val createGroupData = for {
          ov <- s.orgValue
          nv <- s.nameValue
          pv <- s.pathValue
        } yield {
          CreateGroupData(
            ov,
            nv,
            pv
          )
        }

        createGroupData match {
          case Some(groupData) =>
            p.changeDialogState(false) >> p.createGroupCallback(groupData.organizationId, groupData.name, groupData.path) >> $.setState(State())
          case None =>
            Callback.log(s"Something failed, the group was no created, wooops!") >> p.changeDialogState(false) >> $.setState(State())
        }
      }
      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "create", disabled = js.defined(!s.inputValidated), primary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Create group"),
        actions = actions,
        open = p.dialogOpened
      )(
        <.div(
          MuiTextField(
            hintText = js.defined("Org"),
            onChange = js.defined(orgValidationCallback),
            errorText = s.orgErrorText
          )()
        ),
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
      dialogOpened: Boolean,
      organizations: List[String],
      changeDialogState: Boolean => Callback,
      createGroupCallback: (GroupOrg, GroupName, GroupPath) => Callback
  ) = component(Props(dialogOpened, organizations, changeDialogState, createGroupCallback))

}
