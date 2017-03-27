package client.components.bootstrap.pagelayouts.lists

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsComponent}
import japgolly.scalajs.react.raw.ReactElement

import scala.scalajs.js

object ListGroup {
  @js.native
  trait Props extends js.Object {
    var fill: Boolean = js.native
    var bsClass: String = js.native
    var componentClass: ReactElement = js.native
  }

  def Props(
        bsClass: String = "list-group",
        componentClass: Option[ReactElement] = None,
        fill: Option[Boolean] = None
           ):Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.bsClass = bsClass
    fill.foreach(p.fill = _)
    componentClass.foreach(p.componentClass = _)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ListGroup)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
