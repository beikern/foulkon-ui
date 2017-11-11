package client.appstate.users

import autowire._
import client.MessageFeedback
import client.appstate.{UserFeedbackReporting, UserWithGroup, Users}
import diode._
import diode.data._
import client.services.AjaxClient
import diode.ActionResult.ModelUpdate
import shared.{Api, FoulkonError}
import shared.entities.{UserDetail, UserGroup}
import shared.responses.users.UserDeleteResponse
import boopickle.Default._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Users actions
case object RefreshUsers                                                                                   extends Action
case class UpdateAllUsers(users: Either[FoulkonError, List[UserWithGroup]])                                extends Action
case class ObtainUserGroupFromExternalId(id: String)                                                       extends Action
case class UpdateUserGroup(externalId: String, userGroup: Either[FoulkonError, (String, List[UserGroup])]) extends Action
case class DeleteUser(id: String)                                                                          extends Action
case class CreateUser(externalId: String, path: String)                                                    extends Action
case class UpdateUserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])                    extends Action
case object RemoveUserFeedbackReporting                                                                    extends Action

// User handlers
class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshUsers =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readUsers()
            .call
            .map(
              userDetailEither =>
                UpdateAllUsers(
                  userDetailEither.map { userDetailList =>
                    userDetailList.map(UserWithGroup(_))
                  }
              )
            )
        )
      )
    case UpdateAllUsers(usersEither) =>
      updated(
        Ready(
          Users(
            usersEither.map { users =>
              Map(users.map(uwg => uwg.user.externalId -> uwg): _*)
            }
          )
        )
      )
    case ObtainUserGroupFromExternalId(id) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readUserGroups(id)
            .call()
            .map(
              userGroupEither =>
                UpdateUserGroup(id,
                                userGroupEither.map(
                                  ug => id -> ug
                                ))
            )
        )
      )
    case UpdateUserGroup(externalId, userGroupEither) => // TODO beikern => ñapa, deshacer
      val userGroupToUpdate: Either[FoulkonError, List[UserGroup]] = userGroupEither.map {
        case ((_, userGroup)) => userGroup
      }
      ModelUpdate(
        modelRW.updated(
          modelRW.value.map { userModel =>
            Users(userModel.users.map { userMap =>
              val userToUpdate = userMap(externalId).copy(group = userGroupToUpdate)
              userMap.updated(externalId, userToUpdate)
            })
          }
        )
      )
    case DeleteUser(externalId) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .deleteUser(externalId)
            .call
            .map {
              case Left(foulkonError)             => UpdateUserFeedbackReporting(Left(foulkonError))
              case Right(UserDeleteResponse(eId)) => UpdateUserFeedbackReporting(Right(s"User $eId deleted successfully!"))
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
              case Left(foulkonError)                    => UpdateUserFeedbackReporting(Left(foulkonError))
              case Right(UserDetail(_, eId, _, _, _, _)) => UpdateUserFeedbackReporting(Right(s"User $eId created successfully!"))
            }
        )
      )
  }
}

class UserFeedbackHandler[M](modelRW: ModelRW[M, Option[UserFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateUserFeedbackReporting(feedback) =>
      updated(Some(UserFeedbackReporting(feedback)), Effect(Future(RefreshUsers)))
    case RemoveUserFeedbackReporting =>
      updated(
        None
      )
  }
}
