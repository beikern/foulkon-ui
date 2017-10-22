package shared.requests.policies

import shared.responses.policies.Statement

case class UpdatePolicyRequest (
  pathParams: UpdatePolicyRequestPathParams,
  body: UpdatePolicyRequestBody
)

case class UpdatePolicyRequestPathParams(
    organizationId: String,
    policyName: String
)
case class UpdatePolicyRequestBody(
    name: String,
    path: String,
    statements: List[Statement]
)
