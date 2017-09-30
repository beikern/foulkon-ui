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

case class CreateGroupData(
  organizationId: String,
  name: String,
  path: String
)

case class UpdateGroupData(
  name: String,
  path: String
)

case class GroupDetail(
  id: String,
  name: String,
  path: String,
  createAt: String,
  updateAt: String,
  urn: String,
  org: String
)