package shared.requests.users

case class ReadUsersRequest(
  offset: Int = 0,
  limit: Int = 10
)
