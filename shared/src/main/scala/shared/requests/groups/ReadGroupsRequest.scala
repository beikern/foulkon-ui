package shared.requests.groups

import shared.requests.PaginatedRequest

case class ReadGroupsRequest(
  offset: Int
) extends PaginatedRequest
