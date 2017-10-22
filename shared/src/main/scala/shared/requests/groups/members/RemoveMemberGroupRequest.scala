package shared.requests.groups.members

case class RemoveMemberGroupRequest(
    pathParams: RemoveMemberGroupRequestPathParams
)

case class RemoveMemberGroupRequestPathParams(
    organizationId: String,
    name: String,
    userId: String
)
