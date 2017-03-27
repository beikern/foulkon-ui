package client.components.bootstrap.buttons

import autowire._
import client.components.bootstrap.styles.BsStyle
import client.services.AjaxClient
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.Api

import boopickle.Default._
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ButtonLoading {

  class Backend(bs: BackendScope[Props, State]) {

    def render(p:Props, s: State): VdomElement = {
      Button(
        Button.Props(
          active = s.isLoading,
          bsStyle = BsStyle.primary,
          disabled = s.isLoading,
          onClick = bs.setState(State(true))
            >> p.cb
            >> CallbackTo(AjaxClient[Api].getUsers().call().foreach(println))
            >> CallbackTo(bs.setState(State(false))
            >> Callback{println("waited 3 seconds")}).flatten.delay(3 seconds)
            >> Callback{}
          ),
        if(!s.isLoading) p.tag else "loading...")
    }
  }

  case class Props(tag: String, cb: Callback)
  case class State(isLoading: Boolean)

  val component =
    ScalaComponent.build[Props]("ButtonLoading")
      .initialState(State(isLoading = false))
      .renderBackend[Backend]
      .build

  def apply(props: Props) = component(props)
}
