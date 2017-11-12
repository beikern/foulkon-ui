package shared.requests.users.groups

import shared.requests.PaginatedRequest

case class ReadUserGroupsRequest (
  userExternalId: String,
  offset: Int = 0
) extends PaginatedRequest
