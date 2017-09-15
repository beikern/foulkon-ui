package shared.utils

import shared._

object FoulkonErrorUtils {
  def parseError(htmlCode: Int, error: String, message: String): FoulkonError = {
    error match {
      case "UnknownApiError"                     => UnknownApiError(htmlCode, error, message)
      case "InvalidParameterError"               => InvalidParameterError(htmlCode, error, message)
      case "UnauthorizedResourcesError"          => UnauthorizedResourcesError(htmlCode, error, message)
      case "AuthenticationApiError"              => AuthenticationApiError(htmlCode, error, message)
      case "UserWithExternalIDNotFound"          => UserWithExternalIDNotFound(htmlCode, error, message)
      case "UserAlreadyExist"                    => UserAlreadyExist(htmlCode, error, message)
      case "GroupWithOrgAndNameNotFound"         => GroupWithOrgAndNameNotFound(htmlCode, error, message)
      case "GroupAlreadyExist"                   => GroupAlreadyExist(htmlCode, error, message)
      case "UserIsAlreadyAMemberOfGroup"         => UserIsAlreadyAMemberOfGroup(htmlCode, error, message)
      case "UserIsNotAMemberOfGroup"             => UserIsNotAMemberOfGroup(htmlCode, error, message)
      case "PolicyIsAlreadyAttachedToGroup"      => PolicyIsAlreadyAttachedToGroup(htmlCode, error, message)
      case "PolicyIsNotAttachedToGroup"          => PolicyIsNotAttachedToGroup(htmlCode, error, message)
      case "PolicyAlreadyExist"                  => PolicyAlreadyExist(htmlCode, error, message)
      case "PolicyWithOrgAndNameNotFound"        => PolicyWithOrgAndNameNotFound(htmlCode, error, message)
      case "ProxyResourceAlreadyExist"           => ProxyResourceAlreadyExist(htmlCode, error, message)
      case "ProxyResourceWithOrgAndNameNotFound" => ProxyResourceWithOrgAndNameNotFound(htmlCode, error, message)
      case "ProxyResourcesRoutesConflict"        => ProxyResourcesRoutesConflict(htmlCode, error, message)
      case "AuthOidcProviderAlreadyExist"        => AuthOidcProviderAlreadyExist(htmlCode, error, message)
      case "AuthOidcProviderWithNameNotFound"    => AuthOidcProviderWithNameNotFound(htmlCode, error, message)
      case "RegexNoMatch"                        => RegexNoMatch(htmlCode, error, message)
      case _                                     => UnmatchedApiError()
    }
  }
}
