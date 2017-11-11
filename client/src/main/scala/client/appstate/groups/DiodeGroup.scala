package client.appstate.groups

import autowire._
import diode._
import diode.data._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import client.MessageFeedback
import client.appstate.{GroupFeedbackReporting, GroupMetadataWithMember, GroupMetadataWithPolicy, Groups}
import client.services.AjaxClient
import shared.{Api, FoulkonError}
import shared.entities.GroupDetail
import shared.requests.groups.members.{MemberGroupRequest, MemberGroupRequestPathParams}
import shared.requests.groups._
import shared.requests.groups.policies.PoliciesAssociatedToGroupRequest
import shared.responses.groups.GroupDeleteResponse

import scala.concurrent.Future

// Group actions
case object RefreshGroups                                                                                      extends Action
case class UpdateAllGroups(groups: Either[FoulkonError, List[GroupDetail]])                                    extends Action
case class UpdateGroup(organizationId: String, originalName: String, updatedName: String, updatedPath: String) extends Action
case class CreateGroup(organizationId: String, name: String, path: String)                                     extends Action
case class DeleteGroup(organizationId: String, name: String)                                                   extends Action
case class RetrieveGroupMemberInfo(id: String, organizationId: String, name: String)                           extends Action
case class UpdateGroupMemberInfo(id: String, groupMetadataWithMember: GroupMetadataWithMember)                 extends Action
case class RetrieveGroupPolicyInfo(id: String, request: PoliciesAssociatedToGroupRequest)                      extends Action
case class UpdateGroupPolicyInfo(id: String, groupMetadataWithPolicy: GroupMetadataWithPolicy)                 extends Action
case class UpdateGroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])                       extends Action
case object RemoveGroupFeedbackReporting                                                                       extends Action

// Group handlers
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
              case Left(foulkonError)                   => UpdateGroupFeedbackReporting(Left(foulkonError))
              case Right(GroupDeleteResponse(org, nam)) => UpdateGroupFeedbackReporting(Right(s"Group $nam with org $org deleted successfully!"))
            }
        )
      )
    case RetrieveGroupMemberInfo(id, organizationId, name) =>
      val request = MemberGroupRequest(
        MemberGroupRequestPathParams(organizationId, name)
      )
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readMemberGroup(request)
            .call
            .map { response =>
              UpdateGroupMemberInfo(id, GroupMetadataWithMember(organizationId, name, response))
            }
        )
      )
    case RetrieveGroupPolicyInfo(id, request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPoliciesAssociatedToGroup(request)
            .call
            .map { response =>
              UpdateGroupPolicyInfo(id, GroupMetadataWithPolicy(request.pathParams.organizationId, request.pathParams.groupName, response))
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