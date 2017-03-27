package client.components.bootstrap.pagelayouts.panels

import client.components.bootstrap.styles.BsStyle
import client.components.bootstrap.styles.BsStyle.BsStyle
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.{ReactNode, ReactText}
import japgolly.scalajs.react.{Children, _}

import scala.scalajs.js

/** React-Bootstrap facade for component [[https://react-bootstrap.github.io/components.html#panels Panel]].
  *
  * JavaScript [[https://github.com/react-bootstrap/react-bootstrap/blob/master/src/Panel.js sources]] for types and further reading.
  */
object Panel {
  @js.native
  trait Props extends js.Object {
    var bsClass: String = js.native
    var bsStyle: String = js.native
    var collapsible: Boolean = js.native
    var defaultExpanded: Boolean = js.native
    var eventKey: Key = js.native
    var expanded: Boolean = js.native
    var footer: ReactNode = js.native
    var header: ReactNode = js.native
    var headerRole: String = js.native
    var id: ReactText = js.native
    var onEnter: js.Function = js.native
    var onEntered: js.Function = js.native
    var onEntering: js.Function = js.native
    var onExit: js.Function = js.native
    var onExiting: js.Function = js.native
    var onExited: js.Function = js.native
    var onSelect: js.Function = js.native
    var panelRole: String = js.native
  }

  def Props(
             bsClass: String = "panel",
             bsStyle: BsStyle = BsStyle.default,
             collapsible: Option[Boolean] = None,
             defaultExpanded: Boolean = false,
             eventKey: Option[Key] = None,
             expanded: Option[Boolean] = None,
             footer: Option[ReactNode] = None,
             header: Option[ReactNode] = None,
             headerRole: Option[String] = None,
             id: Option[String] = None,
             onEnter: Option[Callback] = None,
             onEntered: Option[Callback] = None,
             onEntering: Option[Callback] = None,
             onExit: Option[Callback] = None,
             onExiting: Option[Callback] = None,
             onExited: Option[Callback] = None,
             onSelect: Option[Callback] = None,
             panelRole: Option[String] = None

           ): Props = {
    val p = new js.Object().asInstanceOf[Props]

    // These elements have default value in Panel React component -> https://react-bootstrap.github.io/components.html#panels-props
    p.bsClass = bsClass
    p.bsStyle = bsStyle.toString
    p.defaultExpanded = defaultExpanded

    // These ones don't have default values
    collapsible.foreach(p.collapsible = _)
    eventKey.foreach(p.eventKey = _)
    footer.foreach(p.footer = _)
    header.foreach(p.header = _)
    headerRole.foreach(p.headerRole = _)
    id.foreach(p.id = _)
    onEnter.foreach(enter => p.onEnter = enter.toJsFn)
    onEntered.foreach(entered => p.onEntered = entered.toJsFn)
    onEntering.foreach(entering => p.onEntering = entering.toJsFn)
    onExit.foreach(exit => p.onExit = exit.toJsFn)
    onExiting.foreach(exiting => p.onExiting = exiting.toJsFn)
    onExited.foreach(exited => p.onExited = exited.toJsFn)
    onSelect.foreach(select => p.onSelect = select.toJsFn)
    panelRole.foreach(prole => p.panelRole = prole)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Panel)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
