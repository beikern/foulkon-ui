package shared.responses.users

import shared.responses.PaginatedResponse

case class UserGroupResponse(
    groups: List[UserGroupInfo],
    offset: Int,
    limit: Int,
    total: Int
) extends PaginatedResponse

case class UserGroupInfo(
  org: String,
  name: String,
  joined: String
)