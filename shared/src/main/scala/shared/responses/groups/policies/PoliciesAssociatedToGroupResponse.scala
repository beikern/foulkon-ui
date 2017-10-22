package shared.responses.groups.policies

import shared.responses.PaginatedResponse

case class PoliciesAssociatedToGroupInfo(
  user: String,
  joined: String
)
case class PoliciesAssociatedToGroupResponse(
  policies: List[PoliciesAssociatedToGroupInfo],
  offset: Int,
  limit: Int,
  total: Int
) extends PaginatedResponse
