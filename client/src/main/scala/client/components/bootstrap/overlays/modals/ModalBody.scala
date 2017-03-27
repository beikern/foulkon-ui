package client.components.bootstrap.overlays.modals

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsComponent}
import japgolly.scalajs.react.raw.ReactElement

import scala.scalajs.js

object ModalBody {

  @js.native
  trait Props extends js.Object {
    var bsClass: String = js.native
    var componentClass: ReactElement = js.native
  }

  def Props(
             bsClass: String = "modal-body",
             componentClass: Option[ReactElement] = None
           ): Props = {

    val p = new js.Object().asInstanceOf[Props]
    p.bsClass = bsClass
    componentClass.foreach(p.componentClass = _) // default element type div
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ModalBody)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
