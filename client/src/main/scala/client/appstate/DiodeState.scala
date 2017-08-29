package client
package appstate

import autowire._
import diode._
import diode.data._
import shared.entities.{UserDetail, UserGroup}
import client.services.AjaxClient
import shared.Api

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import diode.ActionResult.ModelUpdate
import diode.react.ReactConnector

case class UserWithGroup(
  user: UserDetail,
  group: Option[Seq[UserGroup]] = None
)

// Actions
case object RefreshUsers extends Action
case class UpdateAllUsers(users: Seq[UserWithGroup]) extends Action
case class ObtainUserGroupFromExternalId(id: String) extends Action
case class UpdateUserGroup(id: String, userGroup: Option[Seq[UserGroup]]) extends Action
// Handlers
class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshUsers =>
      effectOnly(
        Effect(
          AjaxClient[Api].getUsers().call()
            .map(
              ud =>
                UpdateAllUsers(
                  ud.map(
                    UserWithGroup(_)
                  )
                )
            )
        )
      )
    case UpdateAllUsers(users) =>
      updated(Ready(Users(Map(users.map(uwg => uwg.user.externalId -> uwg): _*))))
    case ObtainUserGroupFromExternalId(id) =>
      effectOnly(
        Effect(
          AjaxClient[Api].getUserGroups(id).call()
            .map(
              ug =>
                UpdateUserGroup(id, ug)
            )
        )
      )
    case UpdateUserGroup(id, userGroup) =>
      println("llega a updateusergroup")
      ModelUpdate(
        modelRW.updated(
          modelRW.value.map{
            u =>
              println(s"los users => ${u.users}")
              u.users.get(id).map(_.copy(group = userGroup)) match {
                case Some(uwg) =>
                  println("dentro de some")
                  Users(u.users.updated(id, uwg))
                case None =>
                  println("dentro de none")
                  u
              }
          }
        )
      )
  }
}

// The Root model for the application
case class RootModel(users: Pot[Users])
case class Users(users: Map[String, UserWithGroup])

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override protected def initialModel: RootModel = RootModel(Empty)

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.users)((m, v) => m.copy(users = v)))
  )
}
