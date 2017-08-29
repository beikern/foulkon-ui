package services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import shared.Api
import shared.entities.{UserDetail, UserGroup}

class ApiService (
                   implicit val actorSystem: ActorSystem,
                   implicit val actorMaterializer: ActorMaterializer
                 ) extends Api {

  implicit val ec = actorSystem.dispatcher

  //TODO beikern this is another mock

  val userDetailList =
    List(
      UserDetail(
        "id",
        "externalUserId",
        "/example/SERVER",
        "2015-01-01T12:00:00Z",
        "2015-01-01T12:00:00Z",
        "urn:iws:iam:user/example/admin/user1")
      ,
      UserDetail(
        "id2",
        "externalUserId2ITWORKS",
        "/example/admin",
        "2015-01-02T12:00:00Z",
        "2015-01-02T12:00:00Z",
        "urn:iws:iam:user/example/admin/user2"
        )
    )

  val userGroupList =
    List(
      UserGroup(
        "oneOrg",
        "oneName",
        "2015-01-02T12:00:00Z"
      ),
      UserGroup(
        "twoOrg",
        "twoName",
        "2015-01-02T12:00:00Z"
      )
    )

  val mockUserGroupMap = Map[String, List[UserGroup]](
    "externalUserId" ->  List(
      UserGroup(
        "oneOrgForExternalUserId",
        "OrgOneName",
        "2015-01-02T12:00:00Z"
      ),
      UserGroup(
        "twoOrgForExternalUserId",
        "OrgTwoName",
        "2015-01-02T12:00:00Z"
      ),
      UserGroup(
        "threeOrgForExternalUserId",
        "OrgThreeName",
        "2015-01-02T12:00:00Z"
      )),
    "externalUserId2ITWORKS" ->  List(
      UserGroup(
        "WOLOLO",
        "oneName",
        "2015-01-02T12:00:00Z"
      ),
      UserGroup(
        "HAHA!",
        "twoName",
        "2015-01-02T12:00:00Z"
      ),
      UserGroup(
        "GOTCHA!",
        "twoName",
        "2015-01-02T12:00:00Z"
      ))
  )



  override def getUsers(): Seq[UserDetail] = {
    println("retrieving users")
    // Provide some fake users
    Thread.sleep(2000)
    userDetailList
  }

  override def getUserGroups(id: String): Option[Seq[UserGroup]] = {
    println(s"llamada a getUserGroups(id: String) con id: $id")
    mockUserGroupMap.get(id)
  }
}
