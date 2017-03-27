package client.components.bootstrap.pagelayouts.lists

import client.components.bootstrap.styles.BasicBsStyle.BasicBsStyle
import japgolly.scalajs.react.{Callback, Children, JsComponent}
import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.raw.ReactNode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

object ListGroupItem {
  @js.native
  trait Props extends js.Object {
    var active: js.Any = js.native
    var bsClass: String = js.native
    var bsStyle: String = js.native
    var disabled: js.Any = js.native
    var header: ReactNode = js.native
    var href: String = js.native
    var listItem: Boolean = js.native
    var onClick: js.Function = js.native
    @JSName("type")
    var listItemType: String = js.native
  }

  def Props(
           active: Option[Boolean] = None,
           bsClass: String = "list-group-item",
           bsStyle: Option[BasicBsStyle] = None,
           disabled: Option[Boolean] = None,
           header: Option[ReactNode] = None,
           href: Option[String] = None,
           listItem: Option[Boolean] = None,
           onClick: Option[Callback] = None,
           listItemType: Option[String] = None
           ): Props = {
    val p = (new js.Object).asInstanceOf[Props]

    p.bsClass = bsClass

    active.foreach(ac => p.active = ac)
    bsStyle.foreach(bss => p.bsStyle = bss.toString)
    disabled.foreach(d => p.disabled = d)
    header.foreach(h => p.header = h)
    href.foreach(hr => p.href = hr)
    listItem.foreach(li => p.listItem = li)
    onClick.foreach(oc => p.onClick = oc.toJsFn)
    listItemType.foreach(lit => p.listItemType = lit)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.ListGroupItem)

  def apply(props: Props, children: ChildArg*) = component(props)(children: _*)
}
