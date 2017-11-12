package shared.requests

import shared.utils.Constants._

trait PaginatedRequest {
  val offset: Int
  final val limit: Int = PageSize
}
