package shared.responses

case class UserListAllResponse(
    users: List[String],
    offset: Int,
    limit: Int,
    total: Int
) extends PaginatedResponse
