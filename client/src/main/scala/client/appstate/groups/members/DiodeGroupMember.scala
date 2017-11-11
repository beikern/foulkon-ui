package client.appstate.groups.members

import autowire._

import client.MessageFeedback
import client.appstate.groups.{RetrieveGroupMemberInfo, UpdateGroupMemberInfo, UpdateGroupPolicyInfo}
import client.appstate.{GroupMemberFeedbackReporting, GroupMetadataWithMember, GroupMetadataWithPolicy}
import client.services.AjaxClient
import diode._
import shared.{Api, FoulkonError}
import shared.requests.groups.members.{
  AddMemberGroupRequest,
  AddMemberGroupRequestPathParams,
  RemoveMemberGroupRequest,
  RemoveMemberGroupRequestPathParams
}
import boopickle.Default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future

// Actions
// Group members
case class AddGroupMember(id: String, organizationId: String, name: String, userId: String)    extends Action
case class RemoveGroupMember(id: String, organizationId: String, name: String, userId: String) extends Action
case class UpdateGroupMemberFeedbackReporting(id: String, organizationId: String, name: String, feedback: Either[FoulkonError, MessageFeedback]) extends Action
case object RemoveGroupMemberFeedbackReporting extends Action

// Handlers
class GroupMemberHandler[M](modelRW: ModelRW[M, Map[String, GroupMetadataWithMember]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupMemberInfo(id, groupMetadataWithMember) =>
      updated(
        modelRW.value.updated(id, groupMetadataWithMember)
      )
    case AddGroupMember(id, organizationId, name, userId) =>
      val request = AddMemberGroupRequest(
        AddMemberGroupRequestPathParams(organizationId, name, userId)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .addMemberGroup(request).call
            .map {
              case Left(foulkonError) => UpdateGroupMemberFeedbackReporting(id, organizationId, name, Left(foulkonError))
              case Right(_)           => UpdateGroupMemberFeedbackReporting(id, organizationId, name, Right(s"member $userId associated successfully!"))
            }
        )
      )
    case RemoveGroupMember(id, organizationId, name, userId) =>
      val request = RemoveMemberGroupRequest(
        RemoveMemberGroupRequestPathParams(organizationId, name, userId)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .removeMemberGroup(request)
            .call
            .map {
              case Left(foulkonError) => UpdateGroupMemberFeedbackReporting(id, organizationId, name, Left(foulkonError))
              case Right(_)           => UpdateGroupMemberFeedbackReporting(id, organizationId, name, Right(s"member $userId disassociated successfully!"))
            }
        )
      )
  }
}

class GroupMemberFeedbackHandler[M](modelRW: ModelRW[M, Option[GroupMemberFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupMemberFeedbackReporting(id, org, name, feedback) =>
      updated(Some(GroupMemberFeedbackReporting(feedback)), Effect(Future(RetrieveGroupMemberInfo(id, org, name))))
    case RemoveGroupMemberFeedbackReporting =>
      updated(
        None
      )
  }
}

class GroupPolicyHandler[M](modelRW: ModelRW[M, Map[String, GroupMetadataWithPolicy]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateGroupPolicyInfo(id, groupMetadataWithPolicy) =>
      updated(
        modelRW.value.updated(id, groupMetadataWithPolicy)
      )
  }
}
