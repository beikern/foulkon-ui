package services

import java.util.UUID

import shared._
import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared.requests.groups.{CreateGroupRequest, ReadGroupsRequest, UpdateGroupRequest}
import shared.requests.groups.members.{AddMemberGroupRequest, MemberGroupRequest, RemoveMemberGroupRequest}
import shared.requests.groups.policies.PoliciesAssociatedToGroupRequest
import shared.requests.policies.{CreatePolicyRequest, DeletePolicyRequest, ReadPoliciesRequest, UpdatePolicyRequest}
import shared.requests.users.ReadUsersRequest
import shared.requests.users.groups.ReadUserGroupsRequest
import shared.responses.groups.GroupDeleteResponse
import shared.responses.groups.members.{AddMemberGroupResponse, MemberAssociatedToGroupInfo, RemoveMemberGroupResponse}
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo
import shared.responses.policies.DeletePolicyResponse
import shared.responses.users.UserDeleteResponse
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class ApiServiceMock extends Api {

  override def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]] = ???

  val mockUsers: List[UserDetail] =
    (0 to 550).toList.map { n =>
      val ns = n.toString
      UserDetail(
        UUID.randomUUID().toString,
        ns,
        ns,
        ns,
        ns,
        ns
      )
    }

  override def readUsers(request: ReadUsersRequest): Future[Either[FoulkonError, (TotalUsers, List[UserDetail])]] = {
    println(s"readUsers mock. Request offset: ${request.offset}. Request limit: ${request.limit}")
    val x: Future[Either[FoulkonError, (TotalUsers, List[UserDetail])]] = Future(
      Right(mockUsers.size -> mockUsers.slice(request.offset, request.offset + request.limit)))
    x
  }

  override def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]] = ???

  val mockUsersGroups: List[UserGroup] =
    (0 to 550).toList.map { n =>
      val ns = n.toString
      UserGroup(
        ns,
        ns,
        ns
      )
    }
  override def readUserGroups(request: ReadUserGroupsRequest): Future[Either[FoulkonError, (TotalUserGroups, List[UserGroup])]] = {
    println(s"readUserGroupsMock. Request offset: ${request.offset}. Request limit: ${request.limit}")
    val x: Future[Either[FoulkonError, (TotalUserGroups, List[UserGroup])]] = Future(
      Right(mockUsersGroups.size -> mockUsersGroups.slice(request.offset, request.offset + request.limit)))
    x
  }

  override def createGroup(request: CreateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = ???

  val mockGroups: List[GroupDetail] =
    (0 to 550).toList.map { n =>
      val ns = n.toString
      GroupDetail(
        UUID.randomUUID().toString,
        ns,
        ns,
        ns,
        ns,
        ns,
        ns
      )
    }

  override def readGroups(request: ReadGroupsRequest): Future[Either[FoulkonError, (TotalGroups, List[GroupDetail])]] = {
    println(s"readGroupsMock. Request offset: ${request.offset}. Request limit: ${request.limit}")
    val x: Future[Either[FoulkonError, (TotalGroups, List[GroupDetail])]] = Future(
      Right(mockGroups.size -> mockGroups.slice(request.offset, request.offset + request.limit)))
    x
  }

  override def updateGroup(request: UpdateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = ???

  override def deleteGroup(organizationId: String, name: String): Future[Either[FoulkonError, GroupDeleteResponse]] = ???

  override def readMemberGroup(request: MemberGroupRequest): Future[Either[FoulkonError, (TotalGroupMembers, List[MemberAssociatedToGroupInfo])]] = ???

  override def addMemberGroup(request: AddMemberGroupRequest): Future[Either[FoulkonError, AddMemberGroupResponse]] = ???

  override def removeMemberGroup(request: RemoveMemberGroupRequest): Future[Either[FoulkonError, RemoveMemberGroupResponse]] = ???

  override def readPoliciesAssociatedToGroup(request: PoliciesAssociatedToGroupRequest): Future[Either[FoulkonError, (TotalGroupPolicies, List[PoliciesAssociatedToGroupInfo])]] = ???

  val mockPolicies: List[PolicyDetail] =
    (0 to 35).toList.map { n =>
      val ns = n.toString
      PolicyDetail(
        UUID.randomUUID().toString,
        ns,
        ns,
        ns,
        ns,
        ns,
        ns,
        List()
      )
    }

  override def readPolicies(request: ReadPoliciesRequest): Future[Either[FoulkonError, (TotalPolicies, List[PolicyDetail])]] = {
    println(s"readPolicies mock. Request offset: ${request.offset}. Request limit: ${request.limit}")
    val x: Future[Either[FoulkonError, (TotalPolicies, List[PolicyDetail])]] = Future(
      Right(mockPolicies.size -> mockPolicies.slice(request.offset, request.offset + request.limit)))
    x
  }

  override def createPolicy(request: CreatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]] = ???

  override def deletePolicy(request: DeletePolicyRequest): Future[Either[FoulkonError, DeletePolicyResponse]] = ???

  override def updatePolicy(request: UpdatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]] = ???
}
