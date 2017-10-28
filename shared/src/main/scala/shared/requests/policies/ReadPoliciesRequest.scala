package shared.requests.policies

case class ReadPoliciesRequest(
  offset: Int = 0,
  limit: Int = 10
)
