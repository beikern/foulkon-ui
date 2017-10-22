package shared.requests.groups.policies

case class PoliciesAssociatedToGroupRequest(
  pathParams: PoliciesAssociatedToGroupRequestPathParams
)

case class PoliciesAssociatedToGroupRequestPathParams(
  organizationId: String,
  groupName: String
)
