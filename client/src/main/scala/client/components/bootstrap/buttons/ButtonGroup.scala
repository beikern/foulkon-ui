package client.components.bootstrap.buttons

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react._

import scala.scalajs.js

object ButtonGroup {

  @js.native
  trait Props extends js.Object {
    var block: Boolean = js.native
    var bsClass: String = js.native
    var justified: Boolean = js.native
    var vertical: Boolean = js.native
  }

  def Props(block: Boolean, bsClass: String = "btn-group", justified: Boolean, vertical: Boolean) = {
    val p = new js.Object().asInstanceOf[Props]
    p.block = block
    p.bsClass = bsClass
    p.justified = justified
    p.vertical = vertical
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ButtonGroup)

  def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}

