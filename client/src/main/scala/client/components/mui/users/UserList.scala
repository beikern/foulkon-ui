package client.components.mui
package users

import client.routes.AppRouter.Location
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.UserDetail

object UserList {

  case class Props(
      router: RouterCtl[Location],
      users: List[UserDetail],
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
                p.router,
                userDetail,
                p.deleteUser
              ))
        ).toTagMod
      )
    )
    .build

  def apply(
    router: RouterCtl[Location],
    users: List[UserDetail],
    deleteUser: String => Callback
  ) = UserList(Props(router, users, deleteUser))
}
