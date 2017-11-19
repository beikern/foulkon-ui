package shared.requests.groups.policies

import shared.requests.PaginatedRequest

case class PoliciesAssociatedToGroupRequest(
  pathParams: PoliciesAssociatedToGroupRequestPathParams,
  offset: Int
) extends PaginatedRequest

case class PoliciesAssociatedToGroupRequestPathParams(
  organizationId: String,
  groupName: String
)
