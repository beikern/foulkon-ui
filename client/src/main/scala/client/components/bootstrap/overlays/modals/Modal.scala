package client.components.bootstrap.overlays.modals

import client.components.bootstrap.styles.BasicBsSize.BasicBsSize
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.ReactElement
import japgolly.scalajs.react.{Callback, Children, JsComponent}

import scala.scalajs.js
import scala.scalajs.js.|

/** React-Bootstrap facade for component [[https://react-bootstrap.github.io/components.html#modals-props-modal modal]].
  *
  * JavaScript [[https://github.com/react-bootstrap/react-bootstrap/blob/master/src/Modal.js sources]] for types and further reading.
  */
object Modal {

  type BooleanOrStatic = Boolean | String


  @js.native
  trait Props extends js.Object {
    var animation: Boolean = js.native
    var autoFocus: Boolean = js.native
    var backdrop: BooleanOrStatic = js.native
    var bsClass: String = js.native
    var bsSize: String = js.native
    var dialogClassName: String = js.native
    var dialogComponentClass: ReactElement = js.native
    var enforceFocus: Boolean = js.native
    var keyboard: Boolean = js.native
    var onEnter: js.Function = js.native
    var onEntered: js.Function = js.native
    var onEntering: js.Function = js.native
    var onExit: js.Function = js.native
    var onExited: js.Function = js.native
    var onHide: js.Function = js.native
    var restoreFocus: Boolean = js.native
    var show: Boolean = js.native
  }

  def Props(
           animation: Boolean = true,
           autoFocus: Option[Boolean] = None,
           backdrop: Option[BooleanOrStatic] = None,
           bsClass: String = "modal",
           bsSize: Option[BasicBsSize] = None,
           dialogClassName: Option[String] = None,
           dialogComponentClass: Option[ReactElement] = None,
           enforceFocus: Option[Boolean] = None,
           keyboard: Option[Boolean] = None,
           onEnter: Option[Callback] = None,
           onEntered: Option[Callback] = None,
           onEntering: Option[Callback] = None,
           onExit: Option[Callback] = None,
           onExited: Option[Callback] = None,
           onHide: Option[Callback] = None,
           restoreFocus: Option[Boolean] = None,
           show: Option[Boolean] = None
           ): Props = {

    val p = new js.Object().asInstanceOf[Props]
    p.animation = animation
    p.bsClass = bsClass

    autoFocus.foreach(p.autoFocus = _)
    backdrop.foreach(p.backdrop = _)
    bsSize.foreach(bss => p.bsSize = bss.toString)
    dialogClassName.foreach(p.dialogClassName = _)
    dialogComponentClass.foreach(p.dialogComponentClass = _)
    enforceFocus.foreach(p.enforceFocus = _)
    keyboard.foreach(p.keyboard = _)
    onEnter.foreach(oe => p.onEnter = oe.toJsFn)
    onEntered.foreach(oen => p.onEntered = oen.toJsFn)
    onEntering.foreach(oent => p.onEntering = oent.toJsFn)
    onExit.foreach(oex => p.onExit = oex.toJsFn)
    onExited.foreach(oext => p.onExited = oext.toJsFn)
    onHide.foreach(oh => p.onHide = oh.toJsFn)
    restoreFocus.foreach(p.restoreFocus = _)
    show.foreach(p.show = _)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Modal)

  def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}
