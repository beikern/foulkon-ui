package shared.requests.groups

case class MemberGroupRequest(
  pathParams: MemberGroupRequestPathParams
)

case class MemberGroupRequestPathParams(
  organizationId: String,
  name: String
)
