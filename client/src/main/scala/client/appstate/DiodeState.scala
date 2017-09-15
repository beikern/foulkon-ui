package client
package appstate

import autowire._
import diode._
import diode.data._
import shared.entities.{UserDetail, UserGroup}
import shared.FoulkonError
import client.services.AjaxClient
import shared.Api

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import diode.ActionResult.ModelUpdate
import diode.react.ReactConnector
import shared.responses.FoulkonErrorFromJson

import scala.concurrent.Future

case class UserWithGroup(
    user: UserDetail,
    group: Option[Seq[UserGroup]] = None
)

// Actions
case object RefreshUsers                                                  extends Action
case class UpdateAllUsers(users: Seq[UserWithGroup])                      extends Action
case class ObtainUserGroupFromExternalId(id: String)                      extends Action
case class UpdateUserGroup(id: String, userGroup: Option[Seq[UserGroup]]) extends Action
case class DeleteUser(id: String)                                         extends Action
case class CreateUser(externalId: String, path: String)                   extends Action
case class UpdateUserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback]) extends Action
case object RemoveUserFeedbackReporting extends Action

// Handlers
class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshUsers =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .getUsers()
            .call
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
      updated(
        Ready(
          Users(
            Map(users.map(uwg => uwg.user.externalId -> uwg): _*)
          )
        )
      )
    case ObtainUserGroupFromExternalId(id) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .getUserGroups(id)
            .call()
            .map(
              ug => UpdateUserGroup(id, ug)
            )
        )
      )
    case UpdateUserGroup(id, userGroup) =>
      println("llega a updateusergroup")
      ModelUpdate(
        modelRW.updated(
          modelRW.value.map { u =>
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
    case DeleteUser(id) =>
      updated(
        value.map(
          potUser => Users(potUser.users - id)
        ),
        Effect(
          AjaxClient[Api].deleteUser(id).call.map { ud =>
            UpdateAllUsers(
              ud.map(
                UserWithGroup(_)
              )
            )
          }
        )
      )
    case CreateUser(externalId, path) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .createUser(externalId, path)
            .call
            .map {
              case Left(foulkonError) => UpdateUserFeedbackReporting(Left(foulkonError))
              case Right(UserDetail(_, eId, _, _, _, _)) => UpdateUserFeedbackReporting(Right(s"User $eId created successfully!"))
            }
        )
      )
  }
}

class UserFeedbackHandler[M](modelRW: ModelRW[M, Option[UserFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateUserFeedbackReporting(feedback) =>
      updated(
        Some(UserFeedbackReporting(feedback))
      , Effect(Future(RefreshUsers)))
    case RemoveUserFeedbackReporting =>
      updated(
        None
      )
  }
}

case class UserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])

// The Root model for the application
case class UserModule(users: Pot[Users], feedbackReporting: Option[UserFeedbackReporting])
case class RootModel(userModule: UserModule)
case class Users(users: Map[String, UserWithGroup])

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override protected def initialModel: RootModel = RootModel(UserModule(Empty, None))

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.userModule.users)((m, v) => m.copy(userModule = m.userModule.copy(users = v)))),
    new UserFeedbackHandler(zoomRW(_.userModule.feedbackReporting)((m, v) => m.copy(userModule = m.userModule.copy(feedbackReporting = v))))
  )
}



