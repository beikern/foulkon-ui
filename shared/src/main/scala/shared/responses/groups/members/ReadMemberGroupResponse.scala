package shared.responses.groups.members

import shared.responses.PaginatedResponse

case class MemberAssociatedToGroupInfo(
  user: String,
  joined: String
)
case class MemberGroupResponse(
  members: List[MemberAssociatedToGroupInfo],
  offset: Int,
  limit: Int,
  total: Int
) extends PaginatedResponse
