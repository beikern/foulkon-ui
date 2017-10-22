package shared

sealed trait FoulkonError {
  def htmlCode: Int
  def code: String
  def message: String
}

// Generic API error codes
case class UnknownApiError(htmlCode: Int, code: String, message: String)            extends FoulkonError
case class InvalidParameterError(htmlCode: Int, code: String, message: String)      extends FoulkonError
case class UnauthorizedResourcesError(htmlCode: Int, code: String, message: String) extends FoulkonError

// Authentication API error code
case class AuthenticationApiError(htmlCode: Int, code: String, message: String) extends FoulkonError

// User API error codes
case class UserWithExternalIDNotFound(htmlCode: Int, code: String, message: String) extends FoulkonError
case class UserAlreadyExist(htmlCode: Int, code: String, message: String)           extends FoulkonError

// Group API error codes
case class GroupWithOrgAndNameNotFound(htmlCode: Int, code: String, message: String) extends FoulkonError
case class GroupAlreadyExist(htmlCode: Int, code: String, message: String)           extends FoulkonError

// GroupMembers error codes
case class UserIsAlreadyAMemberOfGroup(htmlCode: Int, code: String, message: String) extends FoulkonError
case class UserIsNotAMemberOfGroup(htmlCode: Int, code: String, message: String)     extends FoulkonError

// GroupPolicies error codes
case class PolicyIsAlreadyAttachedToGroup(htmlCode: Int, code: String, message: String) extends FoulkonError
case class PolicyIsNotAttachedToGroup(htmlCode: Int, code: String, message: String)     extends FoulkonError

// Policy API error codes
case class PolicyAlreadyExist(htmlCode: Int, code: String, message: String)           extends FoulkonError
case class PolicyWithOrgAndNameNotFound(htmlCode: Int, code: String, message: String) extends FoulkonError

// Proxy resources API error codes
case class ProxyResourceAlreadyExist(htmlCode: Int, code: String, message: String)           extends FoulkonError
case class ProxyResourceWithOrgAndNameNotFound(htmlCode: Int, code: String, message: String) extends FoulkonError
case class ProxyResourcesRoutesConflict(htmlCode: Int, code: String, message: String)        extends FoulkonError

// Auth OIDC Provider API error codes
case class AuthOidcProviderAlreadyExist(htmlCode: Int, code: String, message: String)     extends FoulkonError
case class AuthOidcProviderWithNameNotFound(htmlCode: Int, code: String, message: String) extends FoulkonError

// Regex error
case class RegexNoMatch(htmlCode: Int, code: String, message: String) extends FoulkonError

// Unexpected error. Did the API changed???. Check it if you see this error.
case class UnmatchedApiError(htmlCode: Int = 0, code: String = "error", message: String = "error") extends FoulkonError
