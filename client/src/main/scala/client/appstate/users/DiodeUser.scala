package client.appstate.users

import autowire._
import client.MessageFeedback
import client.appstate.{UserFeedbackReporting, Users}
import diode._
import diode.data._
import client.services.AjaxClient
import shared._
import shared.entities.{UserDetail, UserGroup}
import shared.responses.users.UserDeleteResponse
import boopickle.Default._
import client.Constants.PageSize
import shared.requests.users.ReadUsersRequest

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Users actions
case object FetchUsersToReset                                                                              extends Action
case class FetchUsers(request: ReadUsersRequest)                                                           extends Action
case class SetUsers(users: Either[FoulkonError, (TotalUsers, List[UserDetail])])                           extends Action
case class FetchUserGroupFromExternalId(id: String)                                                        extends Action
case class UpdateUserGroup(externalId: String, userGroup: Either[FoulkonError, (String, List[UserGroup])]) extends Action
case class DeleteUser(id: String)                                                                          extends Action
case class CreateUser(externalId: String, path: String)                                                    extends Action
case class UpdateUserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])                    extends Action
case object RemoveUserFeedbackReporting                                                                    extends Action
case class UpdateTotalUsersAndPages(totalPolicies: TotalPolicies)                                          extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage)                                                  extends Action

// User handlers
class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case FetchUsersToReset =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readUsers(ReadUsersRequest(limit = PageSize))
            .call
            .map(SetUsers)
        )
      )
    case FetchUsers(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readUsers(request)
            .call
            .map(SetUsers)
        )
      )
    case SetUsers(users) =>
      users match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(Users(rightResult.map(_._2))),
            Effect(Future(UpdateTotalUsersAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(Users(leftResult.map(_._2))),
            Effect(Future(UpdateTotalUsersAndPages(0)))
          )
      }
//    case ObtainUserGroupFromExternalId(id) =>
//      effectOnly(
//        Effect(
//          AjaxClient[Api]
//            .readUserGroups(id)
//            .call()
//            .map(
//              userGroupEither =>
//                UpdateUserGroup(id,
//                                userGroupEither.map(
//                                  ug => id -> ug
//                                ))
//            )
//        )
//      )
//    case UpdateUserGroup(externalId, userGroupEither) => // TODO beikern => ñapa, deshacer
//      val userGroupToUpdate: Either[FoulkonError, List[UserGroup]] = userGroupEither.map {
//        case ((_, userGroup)) => userGroup
//      }
//      ModelUpdate(
//        modelRW.updated(
//          modelRW.value.map { userModel =>
//            Users(userModel.users.map { userMap =>
//              val userToUpdate = userMap(externalId).copy(group = userGroupToUpdate)
//              userMap.updated(externalId, userToUpdate)
//            })
//          }
//        )
//      )
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
        ) >> Effect(Future(FetchUsersToReset))
          >> Effect(Future(UpdateSelectedPage(0)))
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
        ) >> Effect(Future(FetchUsersToReset))
          >> Effect(Future(UpdateSelectedPage(0)))
      )
  }
}

class UserFeedbackHandler[M](modelRW: ModelRW[M, Option[UserFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateUserFeedbackReporting(feedback) =>
      updated(Some(UserFeedbackReporting(feedback)), Effect(Future(FetchUsersToReset)))
    case RemoveUserFeedbackReporting =>
      updated(
        None
      )
  }
}

class UserPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalPolicies, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalUsersAndPages(totalUsers) =>
      val totalPages        = (totalUsers.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalUsers, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}
