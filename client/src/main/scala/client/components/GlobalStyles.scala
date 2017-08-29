package client.components

import scalacss.ProdDefaults._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(paddingTop(50.px)))
  style(unsafeRoot("nav.zero-margin-bottom")(marginBottom(0.px)))
  style(unsafeRoot("div.card-padded")(paddingTop(30.px)))
  val bootstrapStyles = new BootstrapStyles
}
