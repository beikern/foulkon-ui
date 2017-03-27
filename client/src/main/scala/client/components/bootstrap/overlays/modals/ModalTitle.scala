package client.components.bootstrap.overlays.modals

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.ReactElement
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js

object ModalTitle {

  @js.native
  trait Props extends js.Object {
    var bsClass: String = js.native
    var componentClass: ReactElement = js.native
  }

  def Props(
           bsClass: String = "modal-title",
           componentClass: Option[ReactElement] = None
           ): Props = {

    val p = new js.Object().asInstanceOf[Props]
    p.bsClass = bsClass
    componentClass.foreach(p.componentClass = _) // default h4 element type
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ModalTitle)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
