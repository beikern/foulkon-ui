package client.components.mui
package users

import client.appstate.UserWithGroup
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object UserList {

  case class Props(
    users: Map[String, UserWithGroup],
    updateGroup: String => Callback,
    deleteUser: String => Callback
  )

  private val UserList = ScalaComponent.builder[Props]("UserList")
    .render_P(
      p => {
        <.div(p.users.map(
          udwg =>
            <.div(^.className := "card-padded",
              UserCard(
                udwg._2,
                p.updateGroup,
                p.deleteUser
              )
            )
        ).toTagMod)
      }
    ).build

  def apply(
    users: Map[String, UserWithGroup],
    updateGroup: String => Callback,
    deleteUser: String => Callback
  ) = UserList(Props(users, updateGroup, deleteUser))
}
