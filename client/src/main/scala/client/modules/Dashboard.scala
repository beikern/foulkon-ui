package client.modules

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

object Dashboard {

  case class Props(a: String)
  // create the React component for Dashboard
  private val component = ScalaComponent
    .build[Props]("Dashboard")
    .render_P(
      props => <.div("this is a div. State ", props.a)
    )
    .build

  def apply(a: String) = component(Props(a))
}
