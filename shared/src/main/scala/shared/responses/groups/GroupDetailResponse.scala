package shared.responses.groups

case class GroupDetailResponse(
    id: String,
    name: String,
    path: String,
    createAt: String,
    updateAt: String,
    urn: String,
    org: String
)