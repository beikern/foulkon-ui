package client.components.mui
package users

import client.appstate.UserWithGroup
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.FoulkonError

object UserList {

  case class Props(
      usersEither: Either[FoulkonError, Map[String, UserWithGroup]],
      updateGroup: String => Callback,
      deleteUser: String => Callback
  )

  private val UserList = ScalaComponent
    .builder[Props]("UserList")
    .render_P(
      p => {
        <.div(p.usersEither match {
          case Right(users) =>
            users
              .map(
                udwg =>
                  <.div(^.className := "card-padded",
                        UserCard(
                          udwg._2,
                          p.updateGroup,
                          p.deleteUser
                        ))
              )
              .toTagMod
          case Left(error) => <.div(error.toString) // TODO jcolomer arreglar, mostrar una card de error
        })
      }
    )
    .build

  def apply(
      users: Either[FoulkonError, Map[String, UserWithGroup]],
      updateGroup: String => Callback,
      deleteUser: String => Callback
  ) = UserList(Props(users, updateGroup, deleteUser))
}
