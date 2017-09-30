package clients

import io.circe.generic.auto._
import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.{sttp, _}
import contexts.AkkaContext
import shared.requests.users._
import shared.responses.users._

trait FoulkonUserClient extends FoulkonConfig { self: AkkaContext =>

  val listAllUsersRequest =
    sttp
      .get(uri"http://$foulkonHost:$foulkonPort/api/v1/users?Limit=1000")
      .contentType("application/json")
      .auth
      .basic(foulkonUser, foulkonPassword)
      .response(asJson[UserListAllResponse])

  val userDetailRequest =
    (externalId: String) =>
      sttp
        .get(uri"http://$foulkonHost:$foulkonPort/api/v1/users/$externalId")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[UserDetailResponse])

  val createUserRequest =
    (externalId: String, path: String) =>
      sttp
        .body(
          CreateUserRequestBody(
            externalId,
            path
          ))
        .post(uri"http://$foulkonHost:$foulkonPort/api/v1/users")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[UserDetailResponse])

  val deleteUserRequest =
    (externalId: String) =>
      sttp
        .delete(uri"http://$foulkonHost:$foulkonPort/api/v1/users/$externalId")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .mapResponse(_ => UserDeleteResponse(externalId))

  val getUserGroupRequest =
    (externalId: String) =>
      sttp
        .get(uri"http://$foulkonHost:$foulkonPort/api/v1/users/$externalId/groups?Limit=1000")
        .contentType("application/json")
        .auth
        .basic(foulkonUser, foulkonPassword)
        .response(asJson[UserGroupResponse])
}
