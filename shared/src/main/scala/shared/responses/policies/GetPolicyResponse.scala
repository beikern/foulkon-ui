package shared.responses.policies

case class Statement (
  effect: String, // todo beikern: sealed trait using values allow and denied?
  actions: List[String],
  resources: List[String]
)

case class GetPolicyResponse (
  id: String,
  name: String,
  path: String,
  createAt: String,
  updateAt: String,
  urn: String,
  org: String,
  statements: List[Statement]
)