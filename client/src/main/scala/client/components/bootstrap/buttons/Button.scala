package client.components.bootstrap.buttons

import client.components.bootstrap.styles.BsSize.BsSize
import client.components.bootstrap.styles.BsStyle.BsStyle
import client.components.bootstrap.buttons.Button.ButtonType.ButtonType
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Callback, _}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

object Button {

  object ButtonType extends Enumeration {
    type ButtonType = Value
    val button, reset, submit = Value
  }

  @js.native
  trait Props extends js.Object {
    var active: Boolean = js.native
    var block: Boolean = js.native
    var bsClass: String = js.native
    var bsSize: String = js.native
    var bsStyle: String = js.native
    var disabled: Boolean = js.native
    @JSName("type")
    var buttonType: String = js.native
    var onClick: js.Function = js.native

  }

  // TODO beikern: overload Props method when needed

  def Props(): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p
  }

  def Props(active: Boolean,
            block: Boolean,
            bsClass: String = "btn",
            bsSize: BsSize,
            bsStyle: BsStyle,
            disabled: Boolean,
            buttonType: ButtonType,
            onClick: Callback): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.active = active
    p.block = block
    p.bsClass = bsClass.toString
    p.bsSize = bsSize.toString
    p.bsStyle = bsStyle.toString
    p.disabled = disabled
    p.buttonType = buttonType.toString
    p.onClick = onClick.toJsFn
    p
  }

  def Props(bsStyle: BsStyle,
            onClick: Callback): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.bsStyle = bsStyle.toString
    p.onClick = onClick.toJsFn
    p
  }

  def Props(active: Boolean,
            bsStyle: BsStyle,
            disabled: Boolean,
            onClick: Callback): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.active = active
    p.bsStyle = bsStyle.toString
    p.disabled = disabled
    p.onClick = onClick.toJsFn
    p
  }


  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Button)

  def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}
