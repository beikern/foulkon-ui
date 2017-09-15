package client.components.mui.users

import java.util.regex.Pattern

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, MuiTextField, TouchTapEvent}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}
import shared.entities.CreateUserData

import scala.scalajs.js

object CreateUserForm {

  case class Props(
      dialogOpened: Boolean,
      changeDialogState: Boolean => Callback,
      createUserCallback: (String, String) => Callback
  )
  case class State(
      inputValidated: Boolean = false,
      externalIdValidation: Boolean = false,
      externalIdErrorText: js.UndefOr[VdomNode] = js.undefined,
      externalIdValue: Option[String] = None,
      pathValidation: Boolean = false,
      pathErrorText: js.UndefOr[VdomNode] = js.undefined,
      pathValue: Option[String] = None
  )

  val externalIdPattern: Pattern = "^[\\w+.@=\\-_]+$".r.pattern
  val externalIdMaxLength        = 128

  val pathPattern: Pattern = "^/$|^/[\\w+/\\-_]+\\w+/$".r.pattern
  val pathMaxLength        = 512

  class Backend($ : BackendScope[Props, State]) {

    val externalIdValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                externalIdValidation = false,
                externalIdErrorText = js.defined("Input must be non empty."),
                externalIdValue = None,
                inputValidated = false
            ))
        case value if value.length > externalIdMaxLength =>
          $.modState(
            s =>
              s.copy(
                externalIdValidation = false,
                externalIdErrorText = js.defined(s"Input must have less than $externalIdMaxLength characters."),
                externalIdValue = None,
                inputValidated = false
            ))
        case value if !externalIdPattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                externalIdValidation = false,
                externalIdValue = None,
                externalIdErrorText = js.defined(s"Input does not match the pattern $externalIdPattern. ExternalId example: externalUserId")
            ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                externalIdValidation = true,
                externalIdErrorText = js.undefined,
                externalIdValue = Some(actualValue),
                inputValidated = s.pathValidation
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
                inputValidated = s.externalIdValidation
            ))
      }
    }

    def render(p: Props, s: State) = {
      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        p.changeDialogState(false) >> $.setState(State())
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        val createUserData = for {
          eiv <- s.externalIdValue
          pv  <- s.pathValue
        } yield {
          CreateUserData(
            eiv,
            pv
          )
        }

        createUserData match {
          case Some(userData) =>
            p.changeDialogState(false) >> p.createUserCallback(userData.externalId, userData.path) >> $.setState(State())
          case None =>
            Callback.log(s"Something failed, the user was no created, wooops!") >> p.changeDialogState(false) >> $.setState(State())
        }
      }
      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "create", disabled = js.defined(!s.inputValidated), secondary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Creating user"),
        actions = actions,
        open = p.dialogOpened
      )(
        <.div(
          MuiTextField(
            hintText = js.defined("ExternalId"),
            onChange = js.defined(externalIdValidationCallback),
            errorText = s.externalIdErrorText
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
    .builder[Props]("CreateUserDialog")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      dialogOpened: Boolean,
      changeDialogState: Boolean => Callback,
      createUserCallback: (String, String) => Callback
  ) = component(Props(dialogOpened, changeDialogState, createUserCallback))

}
