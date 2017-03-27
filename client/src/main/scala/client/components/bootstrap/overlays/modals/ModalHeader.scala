package client.components.bootstrap.overlays.modals

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Callback, Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

object ModalHeader {

  @js.native
  trait Props extends js.Object {
    @JSName("aria-label")
    var ariaLabel: String = js.native
    var bsClass: String = js.native
    var closeButton: Boolean = js.native
    var onHide: js.Function = js.native
  }

  def Props(
           ariaLabel: String = "Close",
           bsClass: String = "modal-header",
           closeButton: Boolean = false,
           onHide: Option[Callback] = None
           ): Props = {
    val p = new js.Object().asInstanceOf[Props]
    p.ariaLabel = ariaLabel
    p.bsClass = bsClass
    p.closeButton = closeButton

    onHide.foreach(oh => p.onHide = oh.toJsFn)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ModalHeader)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
