package shared.responses.policies

case class UpdatePolicyResponse(
  id: String,
  name: String,
  path: String,
  createAt: String,
  updateAt: String,
  urn: String,
  org: String,
  statements: List[Statement]
)
