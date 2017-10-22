package shared.requests.groups.members

case class MemberGroupRequest(
  pathParams: MemberGroupRequestPathParams
)

case class MemberGroupRequestPathParams(
  organizationId: String,
  name: String
)
