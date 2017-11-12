package shared.requests.users

import shared.requests.PaginatedRequest

case class ReadUsersRequest(
  offset: Int
) extends PaginatedRequest