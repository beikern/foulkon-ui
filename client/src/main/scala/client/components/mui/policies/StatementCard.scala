package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardHeader, MuiCardText, MuiDivider, MuiFlatButton, MuiTextField}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scalacss.ProdDefaults._

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
  )

  class Backend($ : BackendScope[Props, State]) {

    def render(p: Props, s: State) = {

      <.div(^.className := "card-padded",
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Statement X")).render
          )(),
          MuiCardText()(
            <.div(
              MuiTextField(
                hintText = js.defined("Effect")
              )(),
              <.div(^.className := "card-padded",
                MuiDivider()()
              )
            ),
            <.div(^.className := "card-padded", <.b(s"Actions")).render
            ,<.div(
                MuiTextField(
                  hintText = js.defined("Action")
                )()
              ),
              <.div(
                MuiTextField(
                  hintText = js.defined("Action")
                )()
              ),
              MuiFlatButton(primary = js.defined(true), label = js.defined("add another action"))(),
              <.div(^.className := "card-padded",
                MuiDivider()()
              ),
            <.div(^.className := "card-padded", <.b(s"Resources")).render,
            <.div(
              MuiTextField(
                hintText = js.defined("Resource")
              )()),
              MuiFlatButton(primary = js.defined(true), label = js.defined("add another resource"))()

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

  def apply(

  ) =
    component(
      Props(

      )
    )
}
