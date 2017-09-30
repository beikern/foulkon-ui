package shared.responses.groups

import shared.responses.PaginatedResponse

case class GroupInfo(org: String, name: String)

case class GroupListAllResponse(
    groups: List[GroupInfo],
    offset: Int,
    limit: Int,
    total: Int
) extends PaginatedResponse
