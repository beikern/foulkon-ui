package shared.requests.policies

import shared.responses.policies.Statement

case class CreatePolicyRequest (
  pathParams: CreatePolicyRequestPathParams,
  body: CreatePolicyRequestBody
)

case class CreatePolicyRequestPathParams(
    organizationId: String
)
case class CreatePolicyRequestBody(
    name: String,
    path: String,
    statements: List[Statement]
)
