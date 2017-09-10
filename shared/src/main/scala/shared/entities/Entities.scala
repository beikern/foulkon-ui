package shared.entities

case class UserDetail(
    id: String,
    externalId: String,
    path: String,
    createdAt: String,
    updatedAt: String,
    urn: String
)

case class UserGroup(
    org: String,
    name: String,
    joined: String
)

case class CreateUserData(
  externalId: String,
  path: String
)