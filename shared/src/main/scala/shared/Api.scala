package shared

import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared.requests.groups._
import shared.requests.policies.CreatePolicyRequest
import shared.responses.groups.{AddMemberGroupResponse, GroupDeleteResponse, MemberInfo, RemoveMemberGroupResponse}
import shared.responses.users.UserDeleteResponse

import scala.concurrent.Future


trait Api {
  def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]]
  def readUsers(): Future[Either[FoulkonError, List[UserDetail]]]
  def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]]
  def readUserGroups(externalId: String): Future[Either[FoulkonError, List[UserGroup]]]
  def createGroup(request: CreateGroupRequest): Future[Either[FoulkonError, GroupDetail]]
  def readGroups(): Future[Either[FoulkonError, List[GroupDetail]]]
  def updateGroup(request: UpdateGroupRequest): Future[Either[FoulkonError, GroupDetail]]
  def deleteGroup (organizationId: String, name: String): Future[Either[FoulkonError, GroupDeleteResponse]]
  def readMemberGroup(request: MemberGroupRequest): Future[Either[FoulkonError, List[MemberInfo]]]
  def addMemberGroup(request: AddMemberGroupRequest): Future[Either[FoulkonError, AddMemberGroupResponse]]
  def removeMemberGroup(request: RemoveMemberGroupRequest): Future[Either[FoulkonError, RemoveMemberGroupResponse]]
  def readPolicies(): Future[Either[FoulkonError, List[PolicyDetail]]]
  def createPolicy(request: CreatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]]
}
