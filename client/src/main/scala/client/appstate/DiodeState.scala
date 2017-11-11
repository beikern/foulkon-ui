package client
package appstate

import client.appstate.groups.members.{GroupMemberFeedbackHandler, GroupMemberHandler, GroupPolicyHandler}
import client.appstate.groups.{GroupFeedbackHandler, GroupHandler}
import diode._
import diode.data._
import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared.FoulkonError
import client.appstate.policies.{PolicyFeedbackHandler, PolicyHandler, PolicyPagesAndTotalHandler}
import client.appstate.users.{UserFeedbackHandler, UserHandler}
import diode.react.ReactConnector
import shared.responses.groups.members.MemberAssociatedToGroupInfo
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo
import shared.{SelectedPage, TotalPages, TotalPolicies}

case class UserWithGroup(
    user: UserDetail,
    group: Either[FoulkonError, List[UserGroup]] = Right(List())
)

case class GroupMetadataWithMember(
    organizationId: String,
    name: String,
    memberInfo: Either[FoulkonError, List[MemberAssociatedToGroupInfo]]
)

case class GroupMetadataWithPolicy(
    organizationId: String,
    groupName: String,
    policyInfo: Either[FoulkonError, List[PoliciesAssociatedToGroupInfo]]
)


// Users
case class UserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class Users(users: Either[FoulkonError, Map[String, UserWithGroup]])
case class UserModule(users: Pot[Users], feedbackReporting: Option[UserFeedbackReporting])

// Groups
case class GroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class GroupReporting(feedback: Either[FoulkonError, MessageFeedback])
case class Groups(groups: Either[FoulkonError, List[GroupDetail]])
case class GroupMemberFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class GroupModule(
    groups: Pot[Groups],
    members: Map[String, GroupMetadataWithMember],
    policies: Map[String, GroupMetadataWithPolicy],
    groupFeedbackReporting: Option[GroupFeedbackReporting],
    groupMemberFeedbackReporting: Option[GroupMemberFeedbackReporting]
)

// Policies
case class PolicyFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class Policies(policies: Either[FoulkonError, List[PolicyDetail]])
case class PolicyModule(
    policies: Pot[Policies],
    nPolicies: TotalPolicies,
    totalPages: TotalPages,
    selectedPage: SelectedPage,
    policyFeedbackReporting: Option[PolicyFeedbackReporting]
)

// The Root model for the application

case class RootModel(
    userModule: UserModule,
    groupModule: GroupModule,
    policyModule: PolicyModule
)

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override protected def initialModel: RootModel =
    RootModel(UserModule(Empty, None), GroupModule(Empty, Map(), Map(), None, None), PolicyModule(Empty, 0, 0, 0, None))

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.userModule.users)((m, v) => m.copy(userModule = m.userModule.copy(users = v)))),
    new UserFeedbackHandler(zoomRW(_.userModule.feedbackReporting)((m, v) => m.copy(userModule = m.userModule.copy(feedbackReporting = v)))),
    new GroupHandler(zoomRW(_.groupModule.groups)((m, v) => m.copy(groupModule = m.groupModule.copy(groups = v)))),
    new GroupFeedbackHandler(
      zoomRW(_.groupModule.groupFeedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(groupFeedbackReporting = v)))),
    new GroupMemberHandler(zoomRW(_.groupModule.members)((m, v) => m.copy(groupModule = m.groupModule.copy(members = v)))),
    new GroupMemberFeedbackHandler(
      zoomRW(_.groupModule.groupMemberFeedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(groupMemberFeedbackReporting = v)))),
    new GroupPolicyHandler(zoomRW(_.groupModule.policies)((m, v) => m.copy(groupModule = m.groupModule.copy(policies = v)))),
    new PolicyHandler(zoomRW(_.policyModule.policies)((m, v) => m.copy(policyModule = m.policyModule.copy(policies = v)))),
    new PolicyPagesAndTotalHandler(zoomRW(root => (root.policyModule.nPolicies, root.policyModule.totalPages, root.policyModule.selectedPage))((m, v) =>
      m.copy(policyModule = m.policyModule.copy(nPolicies = v._1, totalPages = v._2, selectedPage = v._3)))),
    new PolicyFeedbackHandler(
      zoomRW(_.policyModule.policyFeedbackReporting)((m, v) => m.copy(policyModule = m.policyModule.copy(policyFeedbackReporting = v))))
  )
}
