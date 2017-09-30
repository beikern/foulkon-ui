package clients

import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.{sttp, _}
import contexts.AkkaContext
import io.circe.generic.auto._
import shared.requests.groups.{CreateGroupRequest, UpdateGroupRequest}
import shared.responses.groups._

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
}
