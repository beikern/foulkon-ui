package services

import akka.actor.ActorSystem
import shared.Api
import shared.entities.{UserDetail, UserGroup}
import io.circe.generic.auto._
import shared.responses.{FoulkonErrorFromJson, UserDeleteResponse, UserDetailResponse}
import cats.implicits._
import clients.FoulkonClient
import contexts.AkkaContext
import io.circe.parser._
import shared.FoulkonError
import shared.utils.FoulkonErrorUtils

import scala.concurrent.Future

class ApiService(
    implicit val actorSystem: ActorSystem
) extends Api with AkkaContext with FoulkonClient {

  //TODO beikern this is another mock

  var userDetailList =
    List(
      UserDetail("id", "externalUserId", "/example/SERVER", "2015-01-01T12:00:00Z", "2015-01-01T12:00:00Z", "urn:iws:iam:user/example/admin/user1"),
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
    "externalUserId" -> List(
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
      )
    ),
    "externalUserId2ITWORKS" -> List(
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
      )
    )
  )

  override def getUsers(): Future[Either[FoulkonError, List[UserDetail]]] = {
    println("retrieving users")

    val listAllUserResponse = listAllUsersRequest.send().map { request =>
      request.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get
          }
        )
    }

      val apiResult: Future[Either[FoulkonError, List[UserDetailResponse]]] = listAllUserResponse
      .flatMap { userListAllEither =>
        userListAllEither
          .map {
            _.users.map { userExternalId =>
              userDetailRequest(userExternalId).send().map { request =>
                request.body
                  .bimap(
                    fa = error => {
                      val decodeError = decode[FoulkonErrorFromJson](error)
                        .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
                      FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
                    },
                    fb = responseEither => {
                      responseEither.right.get
                    }
                  )
              }
            }
          }
          .bitraverse(
            f => Future.successful(f),
            g => Future.sequence(g)
          )
          .map(_.sequenceU)
          .map(_.map(_.flatten))
          .map(_.sequenceU)
      }

    apiResult.map {
      _.map {
        userDetailResponseList =>
          userDetailResponseList.map{
            userDetailResponse =>
              UserDetail(
                userDetailResponse.id,
                userDetailResponse.externalId,
                userDetailResponse.path,
                userDetailResponse.createAt,
                userDetailResponse.updateAt,
                userDetailResponse.urn
              )
          }
      }
    }
  }

  override def getUserGroups(id: String): Option[List[UserGroup]] = {
    println(s"llamada a getUserGroups(id: String) con id: $id")
    mockUserGroupMap.get(id)
  }

  override def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]] = {
    deleteUserRequest(externalId).send().map { request =>
      request.body
        .leftMap{
          error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
      }
    }
  }

  override def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]] = {
    createUserRequest(externalId, path).send().map { request =>
      request.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            val response = responseEither.right.get
            UserDetail(
              response.id,
              response.externalId,
              response.path,
              response.createAt,
              response.updateAt,
              response.urn
            )
          }
        )
    }
  }

}
