package client.components.bootstrap.navigation

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, _}

import scala.scalajs.js
object NavbarHeader {
  @js.native
  trait Props extends js.Object {
    var bsClass: String = js.native
  }

  def Props(
           bsClass: String = "navbar-header"
           ) = {
    val p = new js.Object().asInstanceOf[Props]
    p.bsClass = bsClass
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.NavbarHeader)
  def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}
