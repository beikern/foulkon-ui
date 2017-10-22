package shared.requests.groups.members

case class AddMemberGroupRequest(
  pathParams: AddMemberGroupRequestPathParams
)

case class AddMemberGroupRequestPathParams(
  organizationId: String,
  name: String,
  userId: String
)
