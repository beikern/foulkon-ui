package client
package appstate

import autowire._
import diode._
import diode.data._
import shared.entities.{GroupDetail, UserDetail, UserGroup}
import shared.FoulkonError
import client.services.AjaxClient
import shared.Api
import cats.syntax.either._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import diode.ActionResult.ModelUpdate
import diode.react.ReactConnector
import shared.requests.groups._
import shared.responses.groups.GroupDeleteResponse
import shared.responses.users.UserDeleteResponse

import scala.concurrent.Future

case class UserWithGroup(
    user: UserDetail,
    group: Either[FoulkonError, List[UserGroup]] = Right(List())
)

// Actions
case object RefreshUsers                                                                                   extends Action
case class UpdateAllUsers(users: Either[FoulkonError, List[UserWithGroup]])                                extends Action
case class ObtainUserGroupFromExternalId(id: String)                                                       extends Action
case class UpdateUserGroup(externalId: String, userGroup: Either[FoulkonError, (String, List[UserGroup])]) extends Action
case class DeleteUser(id: String)                                                                          extends Action
case class CreateUser(externalId: String, path: String)                                                    extends Action
case class UpdateUserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])                    extends Action
case object RemoveUserFeedbackReporting                                                                    extends Action

case object RefreshGroups                                                                extends Action
case class UpdateAllGroups(groups: Either[FoulkonError, List[GroupDetail]])              extends Action
case class UpdateGroup(organizationId: String, originalName: String, updatedName: String, updatedPath: String) extends Action
case class CreateGroup(organizationId: String, name: String, path: String)               extends Action
case class DeleteGroup(organizationId: String, name: String) extends Action
case class UpdateGroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback]) extends Action
case object RemoveGroupFeedbackReporting                                                  extends Action

// Handlers
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

// Handlers
class GroupHandler[M](modelRW: ModelRW[M, Pot[Groups]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshGroups =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readGroups()
            .call
            .map(
              groupDetailEither =>
                UpdateAllGroups(
                  groupDetailEither
              )
            )
        )
      )
    case UpdateAllGroups(groups) =>
      updated(
        Ready(
          Groups(groups)
        )
      )
    case UpdateGroup(organizationId, originalName, updatedName, updatedPath) =>
      val updateRequest = UpdateGroupRequest(
        UpdateGroupRequestPathParams(organizationId, originalName),
        UpdateGroupRequestBody(updatedName, updatedPath)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .updateGroup(updateRequest)
            .call
            .map {
              case Left(foulkonError)                          => UpdateGroupFeedbackReporting(Left(foulkonError))
              case Right(GroupDetail(_, nameg, _, _, _, _, _)) => UpdateGroupFeedbackReporting(Right(s"group $nameg updated successfully!"))
            }
        )
      )
    case CreateGroup(organizationId, name, path) =>
      val createRequest = CreateGroupRequest(
        CreateGroupRequestPathParams(organizationId),
        CreateGroupRequestBody(name, path)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .createGroup(createRequest)
            .call
            .map {
              case Left(foulkonError)                          => UpdateGroupFeedbackReporting(Left(foulkonError))
              case Right(GroupDetail(_, nameg, _, _, _, _, _)) => UpdateGroupFeedbackReporting(Right(s"group $nameg created successfully!"))
            }
        )
      )
    case DeleteGroup(organizationId, name) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .deleteGroup(organizationId, name)
            .call
            .map {
              case Left(foulkonError)             => UpdateGroupFeedbackReporting(Left(foulkonError))
              case Right(GroupDeleteResponse(org, nam)) => UpdateGroupFeedbackReporting(Right(s"Group $nam with org $org deleted successfully!"))
            }
        )
      )
  }
}
class GroupFeedbackHandler[M](modelRW: ModelRW[M, Option[GroupFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupFeedbackReporting(feedback) =>
      updated(Some(GroupFeedbackReporting(feedback)), Effect(Future(RefreshGroups)))
    case RemoveGroupFeedbackReporting =>
      updated(
        None
      )
  }
}

// Users
case class UserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class Users(users: Either[FoulkonError, Map[String, UserWithGroup]])
case class UserModule(users: Pot[Users], feedbackReporting: Option[UserFeedbackReporting])

// Groups
case class GroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class GroupReporting(feedback: Either[FoulkonError, MessageFeedback])
case class Groups(groups: Either[FoulkonError, List[GroupDetail]])
case class GroupModule(groups: Pot[Groups], feedbackReporting: Option[GroupFeedbackReporting])

// The Root model for the application

case class RootModel(userModule: UserModule, groupModule: GroupModule)

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override protected def initialModel: RootModel = RootModel(UserModule(Empty, None), GroupModule(Empty, None))

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.userModule.users)((m, v) => m.copy(userModule = m.userModule.copy(users = v)))),
    new UserFeedbackHandler(zoomRW(_.userModule.feedbackReporting)((m, v) => m.copy(userModule = m.userModule.copy(feedbackReporting = v)))),
    new GroupHandler(zoomRW(_.groupModule.groups)((m, v) => m.copy(groupModule = m.groupModule.copy(groups = v)))),
    new GroupFeedbackHandler(zoomRW(_.groupModule.feedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(feedbackReporting = v))))
  )
}
