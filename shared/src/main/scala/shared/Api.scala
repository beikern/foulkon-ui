package shared

import shared.entities.{UserDetail, UserGroup}
import shared.responses.UserDeleteResponse

import scala.concurrent.Future


trait Api {
  def getUsers(): Future[Either[FoulkonError, List[UserDetail]]]
  def getUserGroups(id: String): Option[List[UserGroup]]
  def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]]
  def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]]
}
