package client.components.bootstrap.navigation

import client.components.bootstrap.navigation.NavBar.BsStyle.BsStyle
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.ReactElement
import japgolly.scalajs.react.{Callback, Children, _}

import scala.scalajs.js

object NavBar {

  object BsStyle extends Enumeration {
    type BsStyle = Value
    val default, inverse = Value
  }

  @js.native
  trait Props extends js.Object {
    var bsStyle: String = js.native
//    var collapseOnSelect: Boolean = js.native
    var componentClass: ReactElement = js.native
    var expanded: Boolean = js.native
    var fixedBottom: Boolean = js.native
    var fixedTop: Boolean = js.native
    var fluid: Boolean = js.native
    var inverse: Boolean = js.native
    var onSelect: js.Function = js.native
    var onToggle: js.Function = js.native
    var role: String = js.native
    var staticTop: Boolean = js.native
  }

  def Props(
    bsStyle: BsStyle = BsStyle.default,
    collapseOnSelect: Boolean = false,
    componentClass: Option[ReactElement] = None,
    expanded: Option[Boolean] = None,
    fixedBottom: Boolean = false,
    fixedTop: Boolean = false,
    fluid: Boolean = false,
    inverse: Boolean = false,
    onSelect: Option[Callback] = None,
    onToggle: Option[Callback] = None,
    role: Option[String] = None,
    staticTop : Boolean = false
  ): Props = {
    val p = new js.Object().asInstanceOf[Props]
    p.bsStyle = bsStyle.toString
//    p.collapseOnSelect = collapseOnSelect
    componentClass.foreach(p.componentClass = _)
    expanded.foreach(p.expanded = _)
    p.fixedBottom = fixedBottom
    p.fixedTop = fixedTop
    p.fluid = fluid
    p.inverse = inverse
    onSelect.foreach(select => p.onSelect = select.toJsFn)
    onToggle.foreach(toggle => p.onToggle = toggle.toJsFn)
    role.foreach(p.role = _)
    p.staticTop = staticTop
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Navbar)
    def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}
