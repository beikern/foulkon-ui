package client.appstate.groups

import autowire._
import diode._
import diode.data._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import client.MessageFeedback
import client.appstate.{GroupFeedbackReporting, Groups}
import client.services.AjaxClient
import shared._
import shared.entities.GroupDetail
import shared.requests.groups._
import shared.responses.groups.GroupDeleteResponse
import shared.utils.constants._

import scala.concurrent.Future

// Group actions
case object FetchGroupsToReset                                                                                 extends Action
case class FetchGroups(request: ReadGroupsRequest)                                                             extends Action
case class SetGroups(groups: Either[FoulkonError, (TotalGroups, List[GroupDetail])])                           extends Action
case class UpdateGroup(organizationId: String, originalName: String, updatedName: String, updatedPath: String) extends Action
case class CreateGroup(organizationId: String, name: String, path: String)                                     extends Action
case class DeleteGroup(organizationId: String, name: String)                                                   extends Action
case class UpdateGroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])                       extends Action
case object RemoveGroupFeedbackReporting                                                                       extends Action
case class UpdateTotalGroupsAndPages(totalGroups: TotalGroups)                                                 extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage)                                                      extends Action

// Group handlers
class GroupHandler[M](modelRW: ModelRW[M, Pot[Groups]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case FetchGroupsToReset =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readGroups(ReadGroupsRequest(offset = 0))
            .call
            .map(SetGroups)
        ) >> Effect(
          Future(UpdateSelectedPage(0))
        )
      )
    case FetchGroups(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readGroups(request)
            .call
            .map(SetGroups)
        )
      )

    case SetGroups(groups) =>
      groups match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(Groups(rightResult.map(_._2))),
            Effect(Future(UpdateTotalGroupsAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(Groups(leftResult.map(_._2))),
            Effect(Future(UpdateTotalGroupsAndPages(0)))
              >> Effect(Future(UpdateSelectedPage(0)))
          )
      }

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
              case Left(foulkonError)                   => UpdateGroupFeedbackReporting(Left(foulkonError))
              case Right(GroupDeleteResponse(org, nam)) => UpdateGroupFeedbackReporting(Right(s"Group $nam with org $org deleted successfully!"))
            }
        )
      )
  }
}
class GroupFeedbackHandler[M](modelRW: ModelRW[M, Option[GroupFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupFeedbackReporting(feedback) =>
      updated(Some(GroupFeedbackReporting(feedback)), Effect(Future(FetchGroupsToReset)))
    case RemoveGroupFeedbackReporting =>
      updated(
        None
      )
  }
}

class GroupPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalGroups, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalGroupsAndPages(totalGroups) =>
      val totalPages = (totalGroups.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalGroups, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}