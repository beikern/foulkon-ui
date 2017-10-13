package shared.requests.policies

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
    statements: List[String]
)
