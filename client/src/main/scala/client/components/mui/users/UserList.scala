package client.components.mui.users

import client.appstate.UserWithGroup
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object UserList {
  case class Props(
    users: Map[String, UserWithGroup],
    updateGroup: String => Callback
  )

  private val UserList = ScalaComponent.builder[Props]("UserList")
    .render_P(
      p => {
        <.div(p.users.map(
          udwg =>
            <.div(^.className := "card-padded", UserCard(
              UserCard.Props(udwg._2, p.updateGroup)
            ))
        ).toTagMod)
      }
    ).build

  def apply(users: Map[String, UserWithGroup], updateGroup: String => Callback) = UserList(Props(users, updateGroup))
}
