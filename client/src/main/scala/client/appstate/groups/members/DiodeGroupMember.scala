package client.appstate.groups.members

import autowire._
import client.MessageFeedback
import client.appstate.{GroupMemberFeedbackReporting, GroupMembers}
import client.services.AjaxClient
import diode.data._
import diode.{Effect, _}
import shared._
import shared.requests.groups.members._
import boopickle.Default._
import shared.responses.groups.members.MemberAssociatedToGroupInfo
import shared.utils.constants._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Future

// Actions
// Group members
case object ResetGroupMembers extends Action
case class FetchGroupMembers(request: MemberGroupRequest) extends Action
case class SetGroupMembers(groupMembers: Either[FoulkonError, (TotalGroupMembers, List[MemberAssociatedToGroupInfo])]) extends Action
case class UpdateTotalGroupMembersAndPages(totalGroupMembers: TotalGroupMembers) extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage) extends Action


case class AddGroupMember(organizationId: String, groupName: String, userId: String)    extends Action
case class RemoveGroupMember(organizationId: String, groupName: String, userId: String) extends Action
case class UpdateGroupMemberFeedbackReporting(organizationId: String, name: String, feedback: Either[FoulkonError, MessageFeedback])
    extends Action
case object RemoveGroupMemberFeedbackReporting extends Action

// Handlers
class GroupMemberHandler[M](modelRW: ModelRW[M, Pot[GroupMembers]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case ResetGroupMembers =>
      updated(Empty,
        Effect(Future(UpdateTotalGroupMembersAndPages(0)))
          >> Effect(Future(UpdateSelectedPage(0)))
      )
    case FetchGroupMembers(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
          .readMemberGroup(request)
          .call
          .map(SetGroupMembers)
        )
      )

    case SetGroupMembers(groupMembers) =>
      groupMembers match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(GroupMembers(rightResult.map(_._2))),
            Effect(Future(UpdateTotalGroupMembersAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(GroupMembers(leftResult.map(_._2))),
            Effect(Future(UpdateTotalGroupMembersAndPages(0))) >> Effect (Future(UpdateSelectedPage(0)))
          )
      }

    case AddGroupMember(organizationId, name, userId) =>
      val request = AddMemberGroupRequest(
        AddMemberGroupRequestPathParams(organizationId, name, userId)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .addMemberGroup(request)
            .call
            .map {
              case Left(foulkonError) => UpdateGroupMemberFeedbackReporting(organizationId, name, Left(foulkonError))
              case Right(_)           => UpdateGroupMemberFeedbackReporting(organizationId, name, Right(s"member $userId associated successfully!"))
            }
        )
      )
    case RemoveGroupMember(organizationId, name, userId) =>
      val request = RemoveMemberGroupRequest(
        RemoveMemberGroupRequestPathParams(organizationId, name, userId)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .removeMemberGroup(request)
            .call
            .map {
              case Left(foulkonError) => UpdateGroupMemberFeedbackReporting(organizationId, name, Left(foulkonError))
              case Right(_)           => UpdateGroupMemberFeedbackReporting(organizationId, name, Right(s"member $userId disassociated successfully!"))
            }
        )
      )
  }
}

class GroupMemberFeedbackHandler[M](modelRW: ModelRW[M, Option[GroupMemberFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupMemberFeedbackReporting(org, name, feedback) =>
      updated(Some(GroupMemberFeedbackReporting(feedback)),
        Effect(
          Future(
            ResetGroupMembers
          )
        ) >>
        Effect(
          Future(
            FetchGroupMembers(MemberGroupRequest(MemberGroupRequestPathParams(org, name), offset = 0))
          )
        )
      )
    case RemoveGroupMemberFeedbackReporting =>
      updated(
        None
      )
  }
}

class GroupMembersPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalGroupMembers, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalGroupMembersAndPages(totalGroupMembers) =>
      val totalPages        = (totalGroupMembers.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalGroupMembers, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}
