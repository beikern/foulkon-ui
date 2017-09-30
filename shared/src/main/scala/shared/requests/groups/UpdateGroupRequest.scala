package shared.requests.groups

case class UpdateGroupRequest(
  pathParams: UpdateGroupRequestPathParams,
  body: UpdateGroupRequestBody
)

case class UpdateGroupRequestPathParams(
  organizationId: String,
  name: String
)

case class UpdateGroupRequestBody(
  name: String,
  path: String
)
