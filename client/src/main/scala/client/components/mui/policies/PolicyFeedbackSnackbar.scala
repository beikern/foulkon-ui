package client.components.mui.policies

import chandu0101.scalajs.react.components.materialui.MuiSnackbar
import client.appstate.{GroupFeedbackReporting, PolicyFeedbackReporting, RemoveGroupFeedbackReporting}
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

object PolicyFeedbackSnackbar {

  case class Props(proxy: ModelProxy[Option[PolicyFeedbackReporting]])
  case class State(isOpen: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {

    def onRequestCloseCallback(deleteFeedback: Callback)(s: String): Callback = {
      deleteFeedback >> $.modState(s => s.copy(isOpen = false))
    }
    def render(p: Props, s: State) = {
      val feedback = p.proxy.value

      val msgToShow = feedback.map { userFeedback =>
        userFeedback.feedback match {
          case Right(successfulMessage) =>
            successfulMessage
          case Left(foulkonError) =>
            s"${foulkonError.htmlCode}: ${foulkonError.code}. ${foulkonError.message}"
        }
      }

      msgToShow match {
        case Some(msg) =>
          MuiSnackbar(
            open = s.isOpen,
            autoHideDuration = js.defined(4000),
            message = <.div(msg),
            onRequestClose = js.defined(onRequestCloseCallback(p.proxy.dispatchCB(RemoveGroupFeedbackReporting)) _)
          )()
        case None =>
          MuiSnackbar(
            open = false,
            autoHideDuration = js.defined(4000),
            message = <.div(),
            onRequestClose = js.defined(onRequestCloseCallback(p.proxy.dispatchCB(RemoveGroupFeedbackReporting)) _)
          )()
      }
    }
  }

  val component = ScalaComponent
    .builder[Props]("PolicyFeedbackSnackbar")
    .initialState(State())
    .renderBackend[Backend]
    .componentWillReceiveProps { x =>
      x.nextProps.proxy.value match {
        case Some(_) => x.setState(State(true))
        case None    => x.setState(State())
      }
    }
    .build

  def apply(proxy: ModelProxy[Option[PolicyFeedbackReporting]]) = component(Props(proxy))
}
