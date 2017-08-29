package shared

import shared.entities.{UserDetail, UserGroup}

trait Api {
  def getUsers(): Seq[UserDetail]
  def getUserGroups(id: String): Option[Seq[UserGroup]]
}
