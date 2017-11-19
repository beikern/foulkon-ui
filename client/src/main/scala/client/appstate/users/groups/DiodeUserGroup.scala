package client.appstate.users.groups

import autowire._
import boopickle.Default._
import client.appstate.UserGroups
import client.appstate.users.groups
import client.services.AjaxClient
import diode.data._
import diode.{Effect, _}
import shared._
import shared.entities.UserGroup
import shared.requests.users.groups.ReadUserGroupsRequest
import shared.utils.constants._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

case object ResetUserGroups                                                                    extends Action
case class FetchUserGroups(request: ReadUserGroupsRequest)                                     extends Action
case class UpdateTotalUserGroupsAndPages(totalPolicies: TotalUserGroups)                       extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage)                                      extends Action
case class SetUserGroups(userGroups: Either[FoulkonError, (TotalUserGroups, List[UserGroup])]) extends Action

// User Groups handlers
class UserGroupHandler[M](modelRW: ModelRW[M, Pot[UserGroups]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case ResetUserGroups =>
      updated(Empty,
        Effect(Future(UpdateTotalUserGroupsAndPages(0)))
        >> Effect(Future(groups.UpdateSelectedPage(0)))
      )
    case FetchUserGroups(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readUserGroups(request)
            .call
            .map(SetUserGroups)
        )
      )
    case SetUserGroups(userGroups) =>
      userGroups match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(UserGroups(rightResult.map(_._2))),
            Effect(Future(UpdateTotalUserGroupsAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(UserGroups(leftResult.map(_._2))),
            Effect(Future(UpdateTotalUserGroupsAndPages(0)))
              >> Effect(Future(UpdateSelectedPage(0)))
          )
      }
  }
}

class UserGroupsPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalUserGroups, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalUserGroupsAndPages(totalUsers) =>
      val totalPages        = (totalUsers.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalUsers, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}
