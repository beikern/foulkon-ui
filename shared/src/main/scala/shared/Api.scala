package shared

import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared.requests.groups._
import shared.requests.groups.members._
import shared.responses.groups.members._
import shared.responses.groups.policies._
import shared.responses.groups._
import shared.requests.groups.policies.PoliciesAssociatedToGroupRequest
import shared.requests.policies._
import shared.requests.users.ReadUsersRequest
import shared.requests.users.groups.ReadUserGroupsRequest
import shared.responses.policies._
import shared.responses.users._

import scala.concurrent.Future

trait Api {
  def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]]
  def readUsers(request: ReadUsersRequest): Future[Either[FoulkonError, (TotalUsers, List[UserDetail])]]
  def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]]
  def readUserGroups(request: ReadUserGroupsRequest): Future[Either[FoulkonError, (TotalUserGroups, List[UserGroup])]]
  def createGroup(request: CreateGroupRequest): Future[Either[FoulkonError, GroupDetail]]
  def readGroups(request: ReadGroupsRequest): Future[Either[FoulkonError, (TotalGroups, List[GroupDetail])]]
  def updateGroup(request: UpdateGroupRequest): Future[Either[FoulkonError, GroupDetail]]
  def deleteGroup (organizationId: String, name: String): Future[Either[FoulkonError, GroupDeleteResponse]]
  def readMemberGroup(request: MemberGroupRequest): Future[Either[FoulkonError, (TotalGroupMembers, List[MemberAssociatedToGroupInfo])]]
  def addMemberGroup(request: AddMemberGroupRequest): Future[Either[FoulkonError, AddMemberGroupResponse]]
  def removeMemberGroup(request: RemoveMemberGroupRequest): Future[Either[FoulkonError, RemoveMemberGroupResponse]]
  def readPoliciesAssociatedToGroup(request: PoliciesAssociatedToGroupRequest): Future[Either[FoulkonError, (TotalGroupPolicies, List[PoliciesAssociatedToGroupInfo])]]
  def readPolicies(request: ReadPoliciesRequest): Future[Either[FoulkonError, (TotalPolicies, List[PolicyDetail])]]
  def createPolicy(request: CreatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]]
  def deletePolicy(request: DeletePolicyRequest): Future[Either[FoulkonError, DeletePolicyResponse]]
  def updatePolicy(request: UpdatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]]
}
