package client.components.bootstrap.navigation

import client.components.bootstrap.navigation.Nav.BsStyle.BsStyle
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.Key
import japgolly.scalajs.react.{Callback, Children, JsComponent}

import scala.scalajs.js

object Nav {
  object BsStyle extends Enumeration {
    type BsStyle = Value
    val tabs, pills = Value
  }

  @js.native
  trait Props extends js.Object {
    var activeHref: String = js.native
    var activeKey: Key = js.native
    var bsClass: String = js.native
    var bsStyle: String = js.native
    var justified: Boolean = js.native
    var navbar: Boolean = js.native
    var onSelect: js.Function = js.native
    var pullLeft: Boolean = js.native
    var pullRight: Boolean = js.native
    var role: String = js.native
    var stacked: Boolean = js.native
  }

  def Props(
    activeHref: Option[String] = None,
    activeKey:Option[Key] = None,
    bsClass:String = "nav",
    bsStyle: Option[BsStyle] = None,
    justified:Boolean = false,
    navbar: Option[Boolean] = None,
    onSelect: Option[Callback] = None,
    pullLeft: Boolean = false,
    pullRight: Boolean = false,
    role: Option[String] = None,
    stacked: Boolean = false): Props = {
    val p = (new js.Object).asInstanceOf[Props]

    activeHref.foreach(p.activeHref = _)
    activeKey.foreach(p.activeKey = _)
    p.bsClass = bsClass
    bsStyle.foreach(style => p.bsStyle = style.toString)
    p.justified = justified
    navbar.foreach(p.navbar = _)
    onSelect.foreach(select => p.onSelect = select.toJsFn)
    p.pullLeft = pullLeft
    p.pullRight = pullRight
    role.foreach(rol => p.role = rol)
    p.stacked = stacked
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Nav)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
