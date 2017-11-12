package client.components.mui.users.groups

import chandu0101.scalajs.react.components.materialui.{MuiCard, MuiCardText}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.UserGroup

object UserGroupCard {

  case class Props(
      userGroupDetail: UserGroup
  )

  case class State()

  class Backend($ : BackendScope[Props, State]) {
    def render(p: Props, s: State) = {
      MuiCard()(
        MuiCardText()(
          <.p(<.b("Org: "), s"${p.userGroupDetail.org}"),
          <.p(<.b("Name: "), s"${p.userGroupDetail.name}"),
          <.p(<.b("Joined: "), s"${p.userGroupDetail.joined}")
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("UserGroupCard")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(
      userGroupDetail: UserGroup
  ) =
    component(
      Props(
        userGroupDetail
      )
    )
}
