package shared.entities

case class UserDetail(
    id: String,
    externalId: String,
    createdAt: Int,
    updatedAt: Int,
    urn: String,
    groups: List[UserGroup]
)

case class UserGroup(
    org: String,
    name: String,
    joined: Int
)

case class User(
    externalId: String
)


