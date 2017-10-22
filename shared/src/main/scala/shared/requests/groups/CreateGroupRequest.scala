package shared.requests.groups

case class CreateGroupRequest(
  pathParams: CreateGroupRequestPathParams,
  body: CreateGroupRequestBody
)

case class CreateGroupRequestPathParams(
  organizationId: String
)
case class CreateGroupRequestBody(
  name: String,
  path: String
)
