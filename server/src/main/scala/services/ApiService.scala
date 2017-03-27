package services

import shared.Api
import shared.entities.User

class ApiService extends Api {

  // TODO beikern this is a mock
  var users = Seq(
    User("Beikern"),
    User("Gate"),
    User("Elon")
  )

  override def getUsers(): Seq[User] = {
    // Provide some fake users
    Thread.sleep(2000)
    users
  }
}
