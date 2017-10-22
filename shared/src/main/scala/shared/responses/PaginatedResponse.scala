package shared.responses

trait PaginatedResponse {
  def offset: Int
  def limit: Int
  def total: Int
}
