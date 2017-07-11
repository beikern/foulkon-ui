package client.components.bootstrap.navigation

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.Key
import japgolly.scalajs.react.{Callback, Children, _}

import scala.scalajs.js

object NavItem {

  @js.native
  trait Props extends js.Object {
    var active: Boolean = js.native
    var disabled: Boolean = js.native
    var eventKey: Key = js.native
    var href: String = js.native
    var onClick: js.Function = js.native
    var onSelect: js.Function = js.native
    var role: String = js.native
  }

  def Props (
    active: Boolean = false,
    disabled: Boolean = false,
    eventKey: Option[Key] = None,
    href: Option[String] = None,
    onClick: Option[Callback] = None,
    onSelect: Option[Callback] = None,
    role: Option[String] = None
  ) = {
    val p = new js.Object().asInstanceOf[Props]
    p.active = active
    p.disabled = disabled
    eventKey.foreach(p.eventKey = _)
    href.foreach(p.href = _)
    onClick.foreach(click => p.onClick = click.toJsFn)
    onSelect.foreach(select => p.onSelect = select.toJsFn)
    role.foreach(p.role = _)
    p
  }
  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.NavItem)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)


}
