package clients

import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.{sttp, _}
import contexts.AkkaContext
import io.circe.generic.auto._
import shared.requests.groups._
import shared.requests.groups.members.{AddMemberGroupRequest, MemberGroupRequest, RemoveMemberGroupRequest}
import shared.requests.groups.policies.PoliciesAssociatedToGroupRequest
import shared.responses.groups._
import shared.responses.groups.members.{AddMemberGroupResponse, MemberGroupResponse, RemoveMemberGroupResponse}
import shared.responses.groups.policies.PoliciesAssociatedToGroupResponse

trait FoulkonGroupClient extends FoulkonConfig { self: AkkaContext =>
  val listAllGroupsRequest =
    sttp
      .get(uri"http://$foulkonHost:$foulkonPort/api/v1/groups?Limit=1000")
      .contentType("application/json")
      .auth
      .basic(foulkonUser, foulkonPassword)
      .response(asJson[GroupListAllResponse])

  val groupDetailRequest =
    (organizationId: String, groupName: String) =>
      sttp
        .get(uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/$organizationId/groups/$groupName")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[GroupDetailResponse])

  val createGroupRequest =
    (request: CreateGroupRequest) =>
      sttp
        .body(request.body)
        .post(uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[GroupDetailResponse])

  val deleteGroupRequest =
    (organizationId: String, name: String) =>
      sttp
        .delete(uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/$organizationId/groups/$name")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .mapResponse(_ => GroupDeleteResponse(organizationId, name))

  val updateGroupRequest =
    (request: UpdateGroupRequest) =>
      sttp
        .body(request.body)
        .put(uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups/${request.pathParams.name}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[GroupDetailResponse])

  val memberGroupRequest =
    (request: MemberGroupRequest) =>
      sttp
        .get(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups/${request.pathParams.name}/users?Limit=1000")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[MemberGroupResponse])

  val addMemberGroupRequest =
    (request: AddMemberGroupRequest) =>
      sttp
        .post(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups/${request.pathParams.name}/users/${request.pathParams.userId}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .mapResponse(_ => AddMemberGroupResponse(request.pathParams.organizationId, request.pathParams.name, request.pathParams.userId))

  val removeMemberGroupRequest =
    (request: RemoveMemberGroupRequest) =>
      sttp
        .delete(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups/${request.pathParams.name}/users/${request.pathParams.userId}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .mapResponse(_ => RemoveMemberGroupResponse(request.pathParams.organizationId, request.pathParams.name, request.pathParams.userId))

  val policiesAssociatedToGroupRequest =
    (request: PoliciesAssociatedToGroupRequest) =>
      sttp
        .get(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/groups/${request.pathParams.groupName}/policies?Limit=1000")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[PoliciesAssociatedToGroupResponse])
}
