package shared.requests.policies

case class GetPolicyRequest (
  pathParams: GetPolicyRequestPathParams
)

case class GetPolicyRequestPathParams(
  organizationId: String,
  policyName: String
)
