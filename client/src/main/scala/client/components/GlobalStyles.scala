package client.components

import scalacss.ProdDefaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(paddingTop(50.px)))
  style(unsafeRoot("nav.zero-margin-bottom")(marginBottom(0.px)))
  style(unsafeRoot("div.card-padded")(paddingTop(15.px), paddingBottom(15.px)))
  style(unsafeRoot("div.card-nested-padded")(paddingTop(15.px), paddingBottom(15.px), paddingRight(15.px), paddingLeft(15.px)))
  val bootstrapStyles = new BootstrapStyles
}
