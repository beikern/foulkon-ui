package client.components.bootstrap.overlays.modals

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsComponent}
import japgolly.scalajs.react.raw.ReactElement

import scala.scalajs.js

object ModalFooter {


  @js.native
  trait Props extends js.Object {
    var bsClass: String = js.native
    var componentClass: ReactElement = js.native
  }

  def Props(
             bsClass: String = "modal-footer",
             componentClass: Option[ReactElement] = None
           ): Props = {

    val p = new js.Object().asInstanceOf[Props]
    p.bsClass = bsClass
    componentClass.foreach(p.componentClass = _) // default div component
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ModalFooter)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)

}
