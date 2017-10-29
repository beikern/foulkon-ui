package client.components.mui.policies.statements

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardHeader, MuiCardText, MuiList, MuiListItem}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.responses.policies.Statement

import scala.scalajs.js

object PolicyStatementCard {

  case class Props(
      number: Int,
      statementDetail: Statement
  )

  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {

      val effectCard =
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Effect")).render
          )(
            MuiCardText()(
              MuiList()(MuiListItem(disabled = js.defined(true), primaryText = p.statementDetail.effect: VdomNode)())
            )
          )
        )

      val actionsToRender = p.statementDetail.actions.map { action =>
        MuiListItem(disabled = js.defined(true), primaryText = action: VdomNode)(): VdomNode
      }

      val actionCard =
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Actions")).render
          )(
            MuiCardText()(
              MuiList()(actionsToRender: _*)
            )
          )
        )

      val resourcesToRender = p.statementDetail.resources.map { resource =>
        MuiListItem(disabled = js.defined(true), primaryText = resource: VdomNode)(): VdomNode
      }

      val resourceCard =
        MuiCard()(
          MuiCardHeader(
            title = <.span(<.b(s"Resources")).render
          )(
            MuiCardText()(
              MuiList()(resourcesToRender: _*)
            )
          )
        )

      MuiCard()(
        <.div(
          MuiCardHeader(
            title = <.span(<.b(s"Statement ${p.number}")).render
          )()
        ),
        <.div(^.className := "card-nested-padded", effectCard),
        <.div(^.className := "card-nested-padded", actionCard),
        <.div(^.className := "card-nested-padded", resourceCard)
      )

    }
  }

  val component = ScalaComponent
    .builder[Props]("PolicyStatementCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      number: Int,
      statementDetail: Statement
  ) =
    component(
      Props(
        number,
        statementDetail
      )
    )
}
