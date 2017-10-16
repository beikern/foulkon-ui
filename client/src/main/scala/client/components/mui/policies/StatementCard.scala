package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFlatButton, MuiGridList, MuiIconButton, MuiTextField, ZDepth}
import client.components.utils.FoulkonMaxLengths._
import client.components.utils.FoulkonRegexPatterns._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.policies.Statement

import scala.collection.mutable
import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object StatementCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(
    key: Int,
    removeStatementCallback: Int => ReactEvent => Callback,
    reportStatementChangesCallback: (Int, Option[Statement]) => Callback
  )

  case class State(
    effectValidated: Boolean = false,
    effectValue: Option[String] = None,
    effectErrorText: js.UndefOr[VdomNode] = js.undefined,
    actionKey: Int = 0,
    actions: mutable.Map[Int, ActionState] = mutable.LinkedHashMap(0 -> ActionState()),
    resources: mutable.Map[Int, ResourceState] = mutable.LinkedHashMap(0 -> ResourceState()),
    resourceKey: Int = 0
  )

  class Backend($ : BackendScope[Props, State]) {

    val effectValidationCallback: (ReactEventFromInput, String) => Callback = { (event: ReactEventFromInput, actualValue: String) =>
      actualValue match {
        case "" =>
          $.modState(
            s =>
              s.copy(
                effectValidated = false,
                effectErrorText = js.defined("Input must be non empty."),
                effectValue = None
              ))
        case value if value != "allow" && value != "deny"=>
          $.modState(
            s =>
              s.copy(
                effectValidated = false,
                effectErrorText = js.defined("""effect must be "allow" or "deny"."""),
                effectValue = None
              ))
        case _ =>
          $.modState(
            s =>
              s.copy(
                effectValidated = true,
                effectErrorText = js.undefined,
                effectValue = Some(actualValue)
              ))
      }
    }

    val addAction = (event: ReactEvent) =>
      $.modState(
        s => s.copy(
          actionKey = s.actionKey + 1,
          actions = s.actions.updated(s.actionKey + 1, ActionState())
        )
      )

    def removeAction(keyToRemove: Int)(event: ReactEvent) =
      $.modState(
        s => s.copy(
          actions = s.actions - keyToRemove
        )
      )

    def actionValidationCallback(keyToUpdate: Int)(event: ReactEventFromInput, actualValue: String): Callback = {
      actualValue match {
        case "" =>
          $.modState(
            s => {
              val emptyAction = ActionState(
                value = None,
                validated = false,
                errorText = js.defined("Input must be non empty.")

              )
              s.copy(
                actions = s.actions.updated(keyToUpdate, emptyAction)
              )
            }
          )
        case value if value.length > actionMaxLength =>
          $.modState(
            s => {
              val tooLongAction = ActionState(
                value = None,
                validated = false,
                errorText = js.defined(s"Input must have less than $actionMaxLength characters.")
              )
              s.copy(
                actions = s.actions.updated(keyToUpdate, tooLongAction)
              )
            }
          )
        case value if !actionPattern.matcher(value).matches || actionExcludePattern.matcher(value).matches =>
          $.modState(
            s => {
              val regexFailedAction = ActionState(
                value = None,
                validated = false,
                errorText = js.defined("""Input does not match regex ^[\w\-_:]+[\w\-_*]+$""")
              )
              s.copy(
                actions = s.actions.updated(keyToUpdate, regexFailedAction)
              )
            }
          )
        case _ =>
          $.modState(
            s => {
              val successfulAction = ActionState(
                value = Some(actualValue),
                validated = true,
                errorText = js.undefined
              )
              s.copy(
                actions = s.actions.updated(keyToUpdate, successfulAction)
              )
            }
          )
      }
    }

    val addResource = (event: ReactEvent) =>
      $.modState(
        s => s.copy(
          resourceKey = s.resourceKey + 1,
          resources = s.resources.updated(s.resourceKey + 1, ResourceState())
        )
      )

    def removeResource(keyToRemove: Int)(event: ReactEvent) =
      $.modState(
        s => s.copy(
          resources = s.resources - keyToRemove
        )
      )

    def resourceValidationCallback(keyToUpdate: Int)(event: ReactEventFromInput, actualValue: String): Callback = {
      actualValue match {
        case "" =>
          $.modState(
            s => {
              val emptyResource = ResourceState(
                value = None,
                validated = false,
                errorText = js.defined("Input must be non empty.")

              )
              s.copy(
                resources = s.resources.updated(keyToUpdate, emptyResource)
              )
            }
          )
        case _ =>
          $.modState(
            s => {
              val successfulResource = ResourceState(
                value = Some(actualValue),
                validated = true,
                errorText = js.undefined
              )
              s.copy(
                resources = s.resources.updated(keyToUpdate, successfulResource)
              )
            }
          )
      }
    }

    def render(p: Props, s: State) = {

      val cardActionsToRender = {
        val addActionButton =
          MuiFlatButton(
            primary = js.defined(true),
            label = js.defined("add another action"),
            onClick = addAction
          )(): VdomNode

        val actions = s.actions.map {
          case (k, _) =>
            if (k != 0) {
              MuiCardText()(
                <.div(
                  MuiTextField(
                    key = js.defined(k.toString),
                    hintText = js.defined("Action"),
                    onChange = js.defined(actionValidationCallback(k) _),
                    errorText = s.actions(k).errorText
                  )(),
                  <.span(
                    MuiIconButton(
                      key = js.defined(k.toString),
                      onClick = js.defined(removeAction(k) _)
                    )(
                      Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "10px", height = "10px"))()
                    ))
                )
              ): VdomNode
            } else {
              MuiCardText()(
                <.div(
                  MuiTextField(
                    key = js.defined(k.toString),
                    hintText = js.defined("Action"),
                    onChange = js.defined(actionValidationCallback(k) _),
                    errorText = s.actions(k).errorText
                  )()
                )
              ): VdomNode
            }
        }.toList :+ addActionButton

        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Actions")).render
          )(
            actions: _*
          )
        )
      }

      val cardResourcesToRender = {
        val addResourceButton =
          MuiFlatButton(
            primary = js.defined(true),
            label = js.defined("add another resource"),
            onClick = addResource
          )(): VdomNode

        val resources = s.resources.map {
          case (k, _) =>
            if (k != 0) {
              MuiCardText()(
                <.div(
                  MuiTextField(
                    key = js.defined(k.toString),
                    hintText = js.defined("Resource"),
                    onChange = js.defined(resourceValidationCallback(k) _),
                    errorText = s.resources(k).errorText
                  )(),
                  <.span(
                    MuiIconButton(
                      key = js.defined(k.toString),
                      onClick = js.defined(removeResource(k) _)
                    )(
                      Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "10px", height = "10px"))()
                    ))
                )
              ): VdomNode
            } else {
              MuiCardText()(
                <.div(
                  MuiTextField(
                    key = js.defined(k.toString),
                    hintText = js.defined("Resource"),
                    onChange = js.defined(resourceValidationCallback(k) _),
                    errorText = s.resources(k).errorText
                  )()
                )
              ): VdomNode
            }
        }.toList :+ addResourceButton

        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Resources (only basic validation!)")).render
          )(
            resources: _*
          )
        )
      }

      <.div(^.className := "card-padded",
        MuiCard(zDepth = js.defined(ZDepth._3))(
          if (p.key != 0) {
            MuiGridList(cellHeight = js.defined(50))(
              MuiCardHeader(
                title = <.span(<.b(s"Statement")).render
              )(),
              <.div(
                Style.editDeleteButton,
                MuiIconButton(
                  onClick = js.defined(p.removeStatementCallback(p.key))
                )(
                  Mui.SvgIcons.ActionDelete.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
                )
              )
            )
          }
          else {
            MuiCardHeader(
              title = <.span(<.b(s"Statement")).render
            )()
          },
          MuiCardText()(
            <.div(
              MuiTextField(
                hintText = js.defined("Effect"),
                onChange = js.defined(effectValidationCallback),
                errorText = s.effectErrorText
              )()
            ),
            <.div(^.className := "card-nested-padded",
              cardActionsToRender
            ),
            <.div(^.className := "card-nested-padded",
              cardResourcesToRender
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
    .shouldComponentUpdate( // This has to be done to avoid infinite loops, see react lifecycle for more information
      f =>
        CallbackTo(f.nextState != f.currentState)
    )
    .componentDidUpdate(
      f => {
        val key = f.currentProps.key
        val allActionsValidated = f.currentState.actions.forall{
          case (_, actionState) => actionState.validated
        }

        val allResourcesValidated = f.currentState.resources.forall{
          case (_, resourceState) => resourceState.validated
        }

        val effectValidated =  f.currentState.effectValidated

        if (allActionsValidated && allResourcesValidated && effectValidated) {
          val actions = f.currentState.actions.toList.map {
            case (_, actionState) =>
              actionState.value.get
          }
          val resources = f.currentState.resources.toList.map {
            case (_, actionState) =>
              actionState.value.get
          }

          val effect = f.currentState.effectValue.get
          f.currentProps.reportStatementChangesCallback(key, Some(Statement(effect, actions, resources)))
        } else {
          f.currentProps.reportStatementChangesCallback(key, None)
        }
      }
    )
    .build

  case class ActionState(
    value: Option[String] = None,
    validated: Boolean = false,
    errorText: js.UndefOr[VdomNode] = js.undefined
  )
  case class ResourceState(
    value: Option[String] = None,
    validated: Boolean = false,
    errorText: js.UndefOr[VdomNode] = js.undefined
  )

  def apply(
    key: Int,
    removeStatementCallback: Int => ReactEvent => Callback,
    reportStatementChangesCallback: (Int, Option[Statement]) => Callback
  ) =
    component(
      Props(
        key,
        removeStatementCallback,
        reportStatementChangesCallback
      )
    )
}
