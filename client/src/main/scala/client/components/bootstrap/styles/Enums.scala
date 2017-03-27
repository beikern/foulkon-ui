package client.components.bootstrap.styles

import client.components.bootstrap.styles.BsStyle.BsStyle

object BsSize extends Enumeration {
  type BsSize = Value
  val large, small, xsmall = Value
}

object BsStyle extends Enumeration {
  type BsStyle = Value
  val success, warning, danger, info, default, primary, link = Value
}

object BasicBsStyle extends Enumeration {
  type BasicBsStyle = Value
  val success, warning, danger, info = Value

}
