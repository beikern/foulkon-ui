package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFlatButton, MuiIconButton, MuiTextField, ZDepth}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scalacss.ProdDefaults._
import client.components.utils.FoulkonRegexPatterns._
import client.components.utils.FoulkonMaxLengths._

import scala.collection.mutable

object StatementCard {

  object Style extends StyleSheet.Inline {

    import dsl._

    val editDeleteButton = style(
      float.right
    )
  }

  case class Props(

  )

  case class State(
    actionKey: Int = 0,
    actions: mutable.Map[Int, ActionState] = mutable.LinkedHashMap(0 -> ActionState())
  )

  class Backend($ : BackendScope[Props, State]) {

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

      <.div(^.className := "card-padded",
        MuiCard(zDepth = js.defined(ZDepth._3))(
          MuiCardHeader(
            title = <.span(<.b(s"Statement X")).render
          )(),
          MuiCardText()(
            <.div(
              MuiTextField(
                hintText = js.defined("Effect")
              )()
            ),
            <.div(^.className := "card-nested-padded",
              cardActionsToRender
            ),
            <.div(^.className := "card-nested-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Resources")).render
                )(),
                MuiCardText()(
                  <.div(
                    MuiTextField(
                      hintText = js.defined("Resource")
                    )()
                  ),
                  <.div(
                    MuiTextField(
                      hintText = js.defined("Resource")
                    )()
                  ),
                  MuiFlatButton(primary = js.defined(true), label = js.defined("add another resource"))()
                )
              )
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
    .build

  case class ActionState(
    value: Option[String] = None,
    validated: Boolean = false,
    errorText: js.UndefOr[VdomNode] = js.undefined
  )

  def apply(

  ) =
    component(
      Props(

      )
    )
}
