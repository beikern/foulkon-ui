package client.components.mui.policies.statements

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.policies.Statement

object PolicyStatementList {

  case class Props(
      statements: List[Statement]
  )
  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      val statementsToRender =
        p.statements.
          zipWithIndex.map{
          case (statement, index) =>
            <.div(^.className := "card-nested-padded",
              PolicyStatementCard(index, statement)
            ):VdomElement
        }
      <.div(statementsToRender: _*)
    }
  }

  val component = ScalaComponent
    .builder[Props]("PolicyStatementList")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
    statements: List[Statement]
  ) = component(
    Props(
      statements
    )
  )
}
