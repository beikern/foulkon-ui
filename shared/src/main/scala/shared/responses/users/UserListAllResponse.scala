package shared.responses.users

import shared.responses.PaginatedResponse

case class UserListAllResponse(
    users: List[String],
    offset: Int,
    limit: Int,
    total: Int
) extends PaginatedResponse
