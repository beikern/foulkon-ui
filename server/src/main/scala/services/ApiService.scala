package services

import akka.actor.ActorSystem
import shared.Api
import shared.entities.{UserDetail, UserGroup}
import io.circe.generic.auto._
import com.softwaremill.sttp.circe._
import com.softwaremill.sttp._
import shared.requests.CreateUserRequest
import shared.responses.{FoulkonErrorFromJson, UserDetailResponse, UserListAllResponse}
import cats.implicits._
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import io.circe.parser._
import shared.FoulkonError
import shared.utils.FoulkonErrorUtils

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ApiService(
    implicit val actorSystem: ActorSystem
) extends Api {
  implicit val sttpBackend      = AkkaHttpBackend.usingActorSystem(actorSystem)
  implicit val executionContext = actorSystem.dispatcher

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

  override def getUsers(): Seq[UserDetail] = {
    println("retrieving users")

    val listAllUsersRequest = sttp
      .get(uri"http://192.168.1.132:8000/api/v1/users?Limit=1000")
      .contentType("application/json")
      .auth
      .basic("admin", "admin")
      .response(asJson[UserListAllResponse])
      .send()

    val listAllUserResponse = listAllUsersRequest.map { request =>
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
              val userDetailRequest = sttp
                .get(uri"http://192.168.1.132:8000/api/v1/users/$userExternalId")
                .contentType("application/json")
                .auth
                .basic("admin", "admin")
                .response(asJson[UserDetailResponse])
                .send()

              userDetailRequest.map { request =>
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
    }.onComplete{
      case Success(Right(va)) => println(va.mkString(", "))
    }
    // Provide some fake users
    Thread.sleep(2000)
    userDetailList
  }

  override def getUserGroups(id: String): Option[Seq[UserGroup]] = {
    println(s"llamada a getUserGroups(id: String) con id: $id")
    mockUserGroupMap.get(id)
  }

  override def deleteUser(id: String): Seq[UserDetail] = {
    println(s"llamada a deleteUser con id $id")
    userDetailList = userDetailList.filterNot(_.externalId == id)
    println(s"lista devuelta a la UI ${userDetailList.mkString(", ")}")
    userDetailList
  }

  override def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]] = {
    userDetailList = userDetailList :+ UserDetail(
      "id",
      externalId,
      path,
      "tomocked",
      "tomocked2.0",
      "aunmasmocked"
    )

    val requestUsers = sttp
      .body(
        CreateUserRequest(
          externalId,
          path
        ))
      .post(uri"http://192.168.1.132:8000/api/v1/users")
      .contentType("application/json")
      .auth
      .basic("admin", "admin")
      .response(asJson[UserDetailResponse])
      .send()

    requestUsers.map { request =>
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
