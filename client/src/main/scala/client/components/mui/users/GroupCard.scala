package client.components.mui
package users

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardHeader, MuiCardText}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.UserGroup

object GroupCard {
  case class Props(
    group:UserGroup
  )
  val groupCard = ScalaComponent.builder[Props]("GroupCard")
  .render_P(
    p =>
      MuiCard()(
        MuiCardHeader(
          title = <.span(<.b(s"${p.group.name}")).render
        )(),
        MuiCardText()(
          <.div(
            <.p(<.b("Organization: "), s"${p.group.org}"),
            <.p(<.b("Joined: "), s"${p.group.joined}")
          )
        )
      )
  ).build

  def apply(group: UserGroup) = groupCard(Props(group))
}


