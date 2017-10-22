package shared.responses.groups.policies

import shared.responses.PaginatedResponse

case class PoliciesAssociatedToGroupInfo(
  policy: String,
  attached: String
)
case class PoliciesAssociatedToGroupResponse(
  policies: List[PoliciesAssociatedToGroupInfo],
  offset: Int,
  limit: Int,
  total: Int
) extends PaginatedResponse
