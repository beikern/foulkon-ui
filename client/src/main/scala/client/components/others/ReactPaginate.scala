package client.components.others

import chandu0101.macros.tojs.JSMacro
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js

case class ReactPaginate(pageCount: Int,
                         pageRangeDisplayed: Int,
                         marginPagesDisplayed: Int,
                         previousLabel: js.UndefOr[VdomElement] = js.undefined,
                         nextLabel: js.UndefOr[VdomElement] = js.undefined,
                         breakLabel: js.UndefOr[VdomElement] = js.undefined,
                         breakClassName: js.UndefOr[String] = js.undefined,
                         onPageChange: js.UndefOr[ReactPaginatePage => Callback] = js.undefined,
                         initialPage: js.UndefOr[Int] = js.undefined,
                         forcePage: js.UndefOr[Int] = js.undefined,
                         disableInitialCallback: js.UndefOr[Boolean] = js.undefined,
                         containerClassName: js.UndefOr[String] = js.undefined,
                         pageClassName: js.UndefOr[String] = js.undefined,
                         pageLinkClassName: js.UndefOr[String] = js.undefined,
                         activeClassName: js.UndefOr[String] = js.undefined,
                         previousClassName: js.UndefOr[String] = js.undefined,
                         nextClassName: js.UndefOr[String] = js.undefined,
                         previousLinkClassName: js.UndefOr[String] = js.undefined,
                         nextLinkClassName: js.UndefOr[String] = js.undefined,
                         disabledClassName: js.UndefOr[String] = js.undefined,
                         hrefBuilder: js.UndefOr[Callback] = js.undefined,
                         extraAriaContext: js.UndefOr[String] = js.undefined) {

  def apply() = {
    val props     = JSMacro[ReactPaginate](this)
    val component = JsComponent[js.Object, Children.None, Null](js.Dynamic.global.ReactPaginate)
    component(props)()
  }
}

trait ReactPaginatePage extends js.Object {
  val selected: Int = js.native
}