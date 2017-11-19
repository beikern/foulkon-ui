package shared.requests

import shared.utils.constants._

trait PaginatedRequest {
  val offset: Int
  final val limit: Int = PageSize
}
