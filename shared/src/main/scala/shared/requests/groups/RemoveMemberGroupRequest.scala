package shared.requests.groups

case class RemoveMemberGroupRequest(
    pathParams: RemoveMemberGroupRequestPathParams
)

case class RemoveMemberGroupRequestPathParams(
    organizationId: String,
    name: String,
    userId: String
)
