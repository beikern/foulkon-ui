package shared

import shared.entities.User

trait Api {

  def getUsers(): Seq[User]

}
