package client.modules

import japgolly.scalajs.react.{ReactComponentB, ReactComponentU, TopNode}
import japgolly.scalajs.react.vdom.prefix_<^._

object Dashboard {

  case class Props(a : String)
  // create the React component for Dashboard
  private val component = ReactComponentB[Props]("Dashboard")
    .render_P(
      props => <.div("this is a div. State ", props.a)
  ).build

  def apply(a: String): ReactComponentU[Props, Unit, Unit, TopNode] = component(Props(a))
}

