package services

import akka.actor.ActorSystem
import cats.implicits._
import clients.FoulkonClient
import contexts.AkkaContext
import io.circe.generic.auto._
import io.circe.parser._
import shared.{Api, FoulkonError}
import shared.entities.{GroupDetail, UserDetail, UserGroup}
import shared.requests.groups.{CreateGroupRequest, CreateGroupRequestBody, CreateGroupRequestPathParams, UpdateGroupRequest}
import shared.responses.FoulkonErrorFromJson
import shared.responses.groups.GroupDeleteResponse
import shared.responses.users._
import shared.utils.FoulkonErrorUtils

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ApiService(
    implicit val actorSystem: ActorSystem
) extends Api
    with AkkaContext
    with FoulkonClient {

  override def readUsers(): Future[Either[FoulkonError, List[UserDetail]]] = {
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
      _.map { userDetailResponseList =>
        userDetailResponseList.map { userDetailResponse =>
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

  override def readUserGroups(externalId: String): Future[Either[FoulkonError, List[UserGroup]]] = {
    getUserGroupRequest(externalId).send().map { request =>
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
        .map { userGroupResponse =>
          userGroupResponse.groups.map { userGroup =>
            UserGroup(
              userGroup.org,
              userGroup.name,
              userGroup.joined
            )
          }
        }
    }
  }

  override def deleteUser(externalId: String): Future[Either[FoulkonError, UserDeleteResponse]] = {
    deleteUserRequest(externalId).send().map { request =>
      request.body
        .leftMap { error =>
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
  override def createGroup(request: CreateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = { // TODO beikern: refactorizar todos los métodos de API para que sigan esta estructura.

    createGroupRequest(request).send().map { request =>
      request.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            val response = responseEither.right.get
            GroupDetail(
              response.id,
              response.name,
              response.path,
              response.createAt,
              response.updateAt,
              response.urn,
              response.org
            )
          }
        )
    }
  }
  def readGroups(): Future[Either[FoulkonError, List[GroupDetail]]] = {
    val listAllGroupsResponse = listAllGroupsRequest.send().map { request =>
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

    listAllGroupsResponse.onComplete {
      case Success(x)   => println(x)
      case Failure(err) => println(err)
    }

    val apiResult = listAllGroupsResponse.flatMap { groupListAllEither =>
      groupListAllEither
        .map {
          _.groups.map { groupInfo =>
            groupDetailRequest(groupInfo.org, groupInfo.name).send().map { request =>
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
      _.map { groupDetailResponseList =>
        groupDetailResponseList.map { groupDetailResponse =>
          GroupDetail(
            groupDetailResponse.id,
            groupDetailResponse.name,
            groupDetailResponse.path,
            groupDetailResponse.createAt,
            groupDetailResponse.updateAt,
            groupDetailResponse.urn,
            groupDetailResponse.org
          )
        }
      }
    }
  }
  override def updateGroup(request: UpdateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = {
    updateGroupRequest(request).send().map { request =>
      request.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            val response = responseEither.right.get
            GroupDetail(
              response.id,
              response.name,
              response.path,
              response.createAt,
              response.updateAt,
              response.urn,
              response.org
            )
          }
        )
    }
  }
  override def deleteGroup(organizationId: String, name: String): Future[Either[FoulkonError, GroupDeleteResponse]] = {
    deleteGroupRequest(organizationId, name).send().map { request =>
      request.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnkownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(request.code, decodeError.code, decodeError.message)
        }
    }
  }
}
