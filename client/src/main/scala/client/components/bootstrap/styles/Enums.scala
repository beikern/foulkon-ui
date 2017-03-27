package client.components.bootstrap.styles

object BsSize extends Enumeration {
  type BsSize = Value
  val large, small, xsmall = Value
}

object BasicBsSize extends Enumeration {
  type BasicBsSize = Value
  val large, small = Value
}

object BsStyle extends Enumeration {
  type BsStyle = Value
  val success, warning, danger, info, default, primary, link = Value
}

object BasicBsStyle extends Enumeration {
  type BasicBsStyle = Value
  val success, warning, danger, info = Value

}
