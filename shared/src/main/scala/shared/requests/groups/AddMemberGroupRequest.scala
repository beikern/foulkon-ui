package shared.requests.groups

case class AddMemberGroupRequest(
  pathParams: AddMemberGroupRequestPathParams
)

case class AddMemberGroupRequestPathParams(
  organizationId: String,
  name: String,
  userId: String
)
