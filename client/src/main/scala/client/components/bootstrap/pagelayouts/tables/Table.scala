package client.components.bootstrap.pagelayouts.tables

import japgolly.scalajs.react.CtorType.ChildArg
import japgolly.scalajs.react.{Children, JsComponent}

import scala.scalajs.js

object Table {
  @js.native
  trait Props extends js.Object {
    var fill: Boolean = js.native
    var bordered: Boolean = js.native
    var bsClass: String = js.native
    var condensed: Boolean = js.native
    var hover: Boolean = js.native
    var responsive: Boolean = js.native
    var striped: Boolean = js.native
  }

  def Props(
           fill: Option[Boolean] = None,
           bordered: Boolean = false,
           bsClass: String = "table",
           condensed: Boolean = false,
           hover: Boolean = false,
           responsive: Boolean = false,
           striped: Boolean = false

           ) = {
    val p = new js.Object().asInstanceOf[Props]
    fill.foreach(p.fill = _)
    p.bordered = bordered
    p.bsClass = bsClass
    p.condensed = condensed
    p.hover = hover
    p.responsive = responsive
    p.striped = striped
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](js.Dynamic.global.ReactBootstrap.Table)

  def apply(props: Props, children: ChildArg*) = component(props)(children:_ *)
}
