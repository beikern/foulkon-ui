package shared.responses.users

case class UserDetailResponse(
    id: String,
    externalId: String,
    path: String,
    urn: String,
    createAt: String,
    updateAt: String
)
