package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.{MuiDialog, MuiFlatButton, MuiTextField, TouchTapEvent}
import client.components.utils.FoulkonMaxLengths._
import client.components.utils.FoulkonRegexPatterns._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, _}
import shared.entities.CreatePolicyData
import shared.requests.policies.{CreatePolicyRequest, CreatePolicyRequestBody, CreatePolicyRequestPathParams}
import shared.responses.policies.Statement

import scala.collection.mutable
import scala.scalajs.js

object CreatePolicyDialog {

  case class Props(
      dialogOpened: Boolean,
      changeDialogState: Boolean => Callback,
      createPolicyCallback: (CreatePolicyRequest) => Callback
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
    pathValue: Option[String] = None,
    statements: mutable.Map[Int, Option[Statement]] =  mutable.LinkedHashMap(0 -> None),
    statementKey: Int = 0
  )

  class Backend($ : BackendScope[Props, State]) {

    val updateInputValidatedCallback = {
      $.modState(
        s => {
          val statementsValidated = s.statements.forall{
            case (_, Some(_)) => true
            case _ => false
          }
          if (s.orgValidation && s.nameValidation && s.pathValidation && statementsValidated) {
            s.copy(inputValidated = true)
          } else {
            s.copy(inputValidated = false)
          }
        }
      ) >> Callback.log("executed update input validated callback")
    }

    val addStatement = (event: ReactEvent) =>
      $.modState(
        s => s.copy(
          statementKey = s.statementKey + 1,
          statements = s.statements.updated(s.statementKey + 1, None)
        )
      ) >> updateInputValidatedCallback

    val removeStatement= (keyToRemove: Int) => (event: ReactEvent) =>
      $.modState(
        s => s.copy(
          statements = s.statements - keyToRemove
        )
      ) >> updateInputValidatedCallback

    def handleStatementResult (key: Int, statement: Option[Statement]): Callback = {
      $.modState(
        s => {
          val updatedStatements = s.statements.updated(key, statement)
          s.copy(
            statements = updatedStatements
          )
        }
      )>> Callback.log("executed handleStatementResult")  >> updateInputValidatedCallback
    }

    val orgValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgErrorText = js.defined("Input must be non empty."),
                orgValue = None
            )) >> updateInputValidatedCallback
        case value if value.length > nameMaxLength =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgErrorText = js.defined(s"Input must have less than $orgMaxLength characters."),
                orgValue = None
            ))
        case value if !orgPattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                orgValidation = false,
                orgValue = None,
                orgErrorText = js.defined(s"Input does not match the pattern $orgPattern. Name example: group1")
            )) >> updateInputValidatedCallback
        case _ =>
          $.modState(
            s =>
              s.copy(
                orgValidation = true,
                orgErrorText = js.undefined,
                orgValue = Some(actualValue)
            )) >> updateInputValidatedCallback
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
                nameValue = None
            )) >> updateInputValidatedCallback
        case value if value.length > nameMaxLength =>
          $.modState(
            s =>
              s.copy(
                nameValidation = false,
                nameErrorText = js.defined(s"Input must have less than $nameMaxLength characters."),
                nameValue = None
            )) >> updateInputValidatedCallback
        case value if !namePattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                nameValidation = false,
                nameValue = None,
                nameErrorText = js.defined(s"Input does not match the pattern $namePattern. Name example: group1")
            )) >> updateInputValidatedCallback
        case _ =>
          $.modState(
            s =>
              s.copy(
                nameValidation = true,
                nameErrorText = js.undefined,
                nameValue = Some(actualValue)
            )) >> updateInputValidatedCallback
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
                pathValue = None
            )) >> updateInputValidatedCallback
        case value if value.length > pathMaxLength =>
          $.modState(
            s =>
              s.copy(pathValidation = false,
                     pathErrorText = js.defined(s"Input must have less than $pathMaxLength characters."),
                     pathValue = None))
        case value if !pathPattern.matcher(value).matches =>
          $.modState(
            s =>
              s.copy(
                pathValidation = false,
                pathErrorText = js.defined(s"input does not match the pattern $pathPattern. Path example: /example/admin"),
                pathValue = None
            )) >> updateInputValidatedCallback
        case _ =>
          $.modState(
            s =>
              s.copy(
                pathValidation = true,
                pathErrorText = js.undefined,
                pathValue = Some(actualValue)
            )) >> updateInputValidatedCallback
      }
    }

    def render(p: Props, s: State) = {
      val cardStatementsToRender = {
        val addStatementButton =
          MuiFlatButton(
            primary = js.defined(true),
            label = js.defined("add another statement"),
            onClick = js.defined(addStatement)
          )(): VdomNode

        println("statements" + s.statements + "validation" + s.inputValidated)

        s.statements.map {
          case (k, _) =>
            StatementCard(k, removeStatement, handleStatementResult): VdomNode
        }.toList :+ addStatementButton
      }

      def handleDialogCancel: TouchTapEvent => Callback = { TouchTapEvent =>
        p.changeDialogState(false) >> $.setState(State())
      }
      def handleDialogSubmit: TouchTapEvent => Callback = { TouchTapEvent =>
        val createPolicyData = for {
          ov <- s.orgValue
          nv <- s.nameValue
          pv <- s.pathValue
        } yield {
          CreatePolicyData(
            ov,
            nv,
            pv
          )
        }
        createPolicyData match {
          case Some(CreatePolicyData(org, name, path)) =>
            val request = CreatePolicyRequest(
              CreatePolicyRequestPathParams(org),
              CreatePolicyRequestBody(name, path, s.statements.toList.map(_._2.get))
            )
            p.changeDialogState(false) >> p.createPolicyCallback(request) >> $.setState(State())
          case None =>
            Callback.log(s"Something failed, the policy was no created, wooops!") >> p.changeDialogState(false) >> $.setState(State())
        }
      }
      val actions: VdomNode = js
        .Array(
          MuiFlatButton(key = "1", label = "cancel", onTouchTap = handleDialogCancel)(),
          MuiFlatButton(key = "2", label = "create", disabled = js.defined(!s.inputValidated), primary = true, onTouchTap = handleDialogSubmit)()
        )
        .toVdomArray

      MuiDialog(
        title = js.defined(s"Create policy"),
        actions = actions,
        open = p.dialogOpened,
        autoScrollBodyContent = js.defined(true)
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
        ),
        <.div(
          cardStatementsToRender: _*
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("CreatePolicyDialog")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
             dialogOpened: Boolean,
             changeDialogState: Boolean => Callback,
             createPolicyCallback: (CreatePolicyRequest) => Callback
  ) = component(Props(dialogOpened, changeDialogState, createPolicyCallback))

}
