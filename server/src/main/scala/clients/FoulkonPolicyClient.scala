package clients

import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.{sttp, _}
import contexts.AkkaContext
import io.circe.generic.auto._
import shared.requests.policies._
import shared.responses.policies._

trait FoulkonPolicyClient extends FoulkonConfig { self: AkkaContext =>
  val listAllPoliciesRequest =
    (request: ReadPoliciesRequest) =>
      sttp
        .get(uri"http://$foulkonHost:$foulkonPort/api/v1/policies?Offset=${request.offset}&Limit=${request.limit}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[PoliciesListAllResponse])
  val policyDetailRequest =
    (request: GetPolicyRequest) =>
      sttp
        .get(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/policies/${request.pathParams.policyName}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[GetPolicyResponse])
  val createPolicyRequest =
    (request: CreatePolicyRequest) =>
      sttp
        .body(request.body)
        .post(uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/policies")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[CreatePolicyResponse])
  val deletePolicyRequest =
    (request: DeletePolicyRequest) =>
      sttp
        .delete(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/policies/${request.pathParams.policyName}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .mapResponse(_ => DeletePolicyResponse(request.pathParams.organizationId, request.pathParams.policyName))
  val updatePolicyRequest =
    (request: UpdatePolicyRequest) =>
      sttp
        .body(request.body)
        .put(
          uri"http://$foulkonHost:$foulkonPort/api/v1/organizations/${request.pathParams.organizationId}/policies/${request.pathParams.policyName}")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[UpdatePolicyResponse])
}
