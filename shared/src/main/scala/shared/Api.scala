package shared

import shared.entities.{GroupDetail, UserDetail, UserGroup}
import shared.requests.groups.{CreateGroupRequest, UpdateGroupRequest}
import shared.responses.groups.GroupDeleteResponse
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
}
