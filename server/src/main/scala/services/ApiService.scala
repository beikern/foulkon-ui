package services

import java.util.UUID

import akka.actor.ActorSystem
import cats.implicits._
import clients.FoulkonClient
import contexts.AkkaContext
import io.circe.generic.auto._
import io.circe.parser._
import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared.requests.groups._
import shared.requests.groups.members._
import shared.requests.groups.policies._
import shared.requests.policies._
import shared.requests.users.ReadUsersRequest
import shared.requests.users.groups.ReadUserGroupsRequest
import shared.responses.FoulkonErrorFromJson
import shared.responses.groups._
import shared.responses.groups.members._
import shared.responses.groups.policies._
import shared.responses.policies._
import shared.responses.users._
import shared.utils.FoulkonErrorUtils
import shared._

import scala.concurrent.Future

class ApiService(
    implicit val actorSystem: ActorSystem
) extends Api
    with AkkaContext
    with FoulkonClient {
  // USERS

  def readUsers(request: ReadUsersRequest): Future[Either[FoulkonError, (TotalUsers, List[UserDetail])]] = {

    val listAllUserResponse = listAllUsersRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
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
              userDetailRequest(userExternalId).send().map { response =>
                response.body
                  .bimap(
                    fa = error => {
                      val decodeError = decode[FoulkonErrorFromJson](error)
                        .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
                      FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
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

    apiResult.flatMap { eitherResult =>
      val mappedResult = eitherResult.map{userDetailResultList =>
        userDetailResultList.map{userDetailResponse =>
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
      listAllUserResponse.map(_.map(_.total).flatMap(total => mappedResult.map(l => total -> l)))
    }
  }
  override def readUserGroups(request: ReadUserGroupsRequest): Future[Either[FoulkonError, (TotalUserGroups, List[UserGroup])]] = {
    getUserGroupRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get
          }
        )
        .map { userGroupResponse =>
          userGroupResponse.total -> userGroupResponse.groups.map { userGroup =>
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
    deleteUserRequest(externalId).send().map { response =>
      response.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
        }
    }
  }

  override def createUser(externalId: String, path: String): Future[Either[FoulkonError, UserDetail]] = {
    createUserRequest(externalId, path).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
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

  // GROUPS
  override def createGroup(request: CreateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = { // TODO beikern: refactorizar todos los métodos de API para que sigan esta estructura.

    createGroupRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
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
  def readGroups(request: ReadGroupsRequest): Future[Either[FoulkonError, (TotalGroups, List[GroupDetail])]] = {
    val listAllGroupsResponse = listAllGroupsRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get
          }
        )
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
                      .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
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

    apiResult.flatMap { eitherResult =>
      val mappedResult = eitherResult.map { groupDetailResponseList =>
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
      listAllGroupsResponse.map(_.map(_.total).flatMap(total => mappedResult.map(l => total -> l)))
    }
  }
  override def updateGroup(request: UpdateGroupRequest): Future[Either[FoulkonError, GroupDetail]] = {
    updateGroupRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
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
    deleteGroupRequest(organizationId, name).send().map { response =>
      response.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
        }
    }
  }
  override def readMemberGroup(request: MemberGroupRequest): Future[Either[FoulkonError, (TotalGroupMembers, List[MemberAssociatedToGroupInfo])]] = {
    memberGroupRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get.total -> responseEither.right.get.members
          }
        )
    }
  }
  override def addMemberGroup(request: AddMemberGroupRequest): Future[Either[FoulkonError, AddMemberGroupResponse]] = {
    addMemberGroupRequest(request).send.map { response =>
      response.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
        }
    }
  }
  override def removeMemberGroup(request: RemoveMemberGroupRequest): Future[Either[FoulkonError, RemoveMemberGroupResponse]] = {
    removeMemberGroupRequest(request).send.map { response =>
      response.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
        }
    }
  }
  def readPoliciesAssociatedToGroup(request: PoliciesAssociatedToGroupRequest): Future[Either[FoulkonError, (TotalGroupPolicies, List[PoliciesAssociatedToGroupInfo])]] = {
    policiesAssociatedToGroupRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get.total -> responseEither.right.get.policies
          }
        )
    }
  }
  // POLICIES
  override def createPolicy(request: CreatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]] = {
    createPolicyRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            val response: CreatePolicyResponse = responseEither.right.get
            PolicyDetail(
              response.id,
              response.name,
              response.path,
              response.createAt,
              response.updateAt,
              response.urn,
              response.org,
              response.statements
            )
          }
        )
    }
  }
  def readPolicies(request: ReadPoliciesRequest): Future[Either[FoulkonError, (TotalPolicies, List[PolicyDetail])]] = {
    val listAllPoliciesResponse = listAllPoliciesRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            responseEither.right.get
          }
        )
    }

    val apiResult = listAllPoliciesResponse.flatMap { policiesListAllEither =>
      policiesListAllEither
        .map { policiesListAllResponse =>
          policiesListAllResponse.policies.map { policyInfo =>
            val policyRequest = GetPolicyRequest(
              GetPolicyRequestPathParams(
                policyInfo.org,
                policyInfo.name
              )
            )
            policyDetailRequest(policyRequest).send().map { request =>
              request.body
                .bimap(
                  fa = error => {
                    val decodeError = decode[FoulkonErrorFromJson](error)
                      .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
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

    apiResult.flatMap { eitherResult =>
      val mappedResult = eitherResult.map { policyDetailResponseList =>
        policyDetailResponseList.map { policyDetailResponse =>
          PolicyDetail(
            policyDetailResponse.id,
            policyDetailResponse.name,
            policyDetailResponse.path,
            policyDetailResponse.createAt,
            policyDetailResponse.updateAt,
            policyDetailResponse.urn,
            policyDetailResponse.org,
            policyDetailResponse.statements
          )
        }
      }
      listAllPoliciesResponse.map(_.map(_.total).flatMap(total => mappedResult.map(l => total -> l)))
    }
  }
  override def deletePolicy(request: DeletePolicyRequest): Future[Either[FoulkonError, DeletePolicyResponse]] = {
    deletePolicyRequest(request).send.map { response =>
      response.body
        .leftMap { error =>
          val decodeError = decode[FoulkonErrorFromJson](error)
            .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
          FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
        }
    }
  }
  override def updatePolicy(request: UpdatePolicyRequest): Future[Either[FoulkonError, PolicyDetail]] = {
    updatePolicyRequest(request).send().map { response =>
      response.body
        .bimap(
          fa = error => {
            val decodeError = decode[FoulkonErrorFromJson](error)
              .getOrElse(FoulkonErrorFromJson("UnknownError", "There was an unknown error."))
            FoulkonErrorUtils.parseError(response.code, decodeError.code, decodeError.message)
          },
          fb = responseEither => {
            val response = responseEither.right.get
            PolicyDetail(
              response.id,
              response.name,
              response.path,
              response.createAt,
              response.updateAt,
              response.urn,
              response.org,
              response.statements
            )
          }
        )
    }
  }
}
