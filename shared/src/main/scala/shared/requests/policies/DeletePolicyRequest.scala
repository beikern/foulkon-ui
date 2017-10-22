package shared.requests.policies

case class DeletePolicyPathParams(
  organizationId: String,
  policyName: String
)
case class DeletePolicyRequest(
  pathParams: DeletePolicyPathParams
)
