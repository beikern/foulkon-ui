package client.components.mui
package users

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.UserDetail

object UserList {

  case class Props(
      users: List[UserDetail],
      updateGroup: String => Callback,
      deleteUser: String => Callback
  )

  private val UserList = ScalaComponent
    .builder[Props]("UserList")
    .render_P(
      p =>
      <.div(^.className := "card-nested-padded",
        p.users.map(
          userDetail =>
            <.div(^.className := "card-padded",
              UserCard(
                userDetail,
                p.updateGroup,
                p.deleteUser
              ))
        ).toTagMod
      )
    )
    .build

  def apply(
    users: List[UserDetail],
    updateGroup: String => Callback,
    deleteUser: String => Callback
  ) = UserList(Props(users, updateGroup, deleteUser))
}
