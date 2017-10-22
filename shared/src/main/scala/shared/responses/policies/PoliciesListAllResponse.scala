package shared.responses.policies

import shared.responses.PaginatedResponse

case class PolicyInfo(
  org: String,
  name: String
)

case class PoliciesListAllResponse (
  policies: List[PolicyInfo],
  offset: Int,
  limit: Int,
  total: Int
) extends PaginatedResponse
