package shared.responses.groups

import shared.responses.PaginatedResponse

case class MemberInfo(
  user: String,
  joined: String
)
case class MemberGroupResponse(
  members: List[MemberInfo],
  offset: Int,
  limit: Int,
  total: Int
) extends PaginatedResponse
