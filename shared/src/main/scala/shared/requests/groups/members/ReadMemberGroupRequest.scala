package shared.requests.groups.members

import shared.requests.PaginatedRequest

case class MemberGroupRequest(
    pathParams: MemberGroupRequestPathParams,
    offset: Int
) extends PaginatedRequest

case class MemberGroupRequestPathParams(
    organizationId: String,
    groupName: String
)
