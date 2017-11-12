package client
package appstate

import client.appstate.groups.members.{GroupMemberFeedbackHandler, GroupMemberHandler, GroupPolicyHandler}
import client.appstate.groups.{GroupFeedbackHandler, GroupHandler}
import diode._
import diode.data._
import shared.entities.{GroupDetail, PolicyDetail, UserDetail, UserGroup}
import shared._
import client.appstate.policies.{PolicyFeedbackHandler, PolicyHandler, PolicyPagesAndTotalHandler}
import client.appstate.users._
import client.appstate.users.groups.{UserGroupHandler, UserGroupsPagesAndTotalHandler}
import diode.react.ReactConnector
import shared.responses.groups.members.MemberAssociatedToGroupInfo
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo

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
case class UserGroups(userGroups: Either[FoulkonError, List[UserGroup]])
case class UserGroupsModule(
    userGroups: Pot[UserGroups],
    userExternalId: Option[String],
    nUserGroups: TotalUserGroups,
    totalPages: TotalPages,
    selectedPage: SelectedPage
)
case class Users(users: Either[FoulkonError, List[UserDetail]])
case class UserModule(
    users: Pot[Users],
    nUsers: TotalPolicies,
    totalPages: TotalPages,
    selectedPage: SelectedPage,
    selectedUserGroups: UserGroupsModule,
    feedbackReporting: Option[UserFeedbackReporting]
)

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
    RootModel(
      UserModule(
        users = Empty,
        nUsers = 0,
        totalPages = 0,
        selectedPage = 0,
        UserGroupsModule(
          userGroups = Empty,
          userExternalId = None,
          nUserGroups = 0,
          totalPages = 0,
          selectedPage = 0
        ),
        feedbackReporting = None
      ),
      GroupModule(
        groups = Empty,
        members = Map(),
        policies = Map(),
        groupFeedbackReporting = None,
        groupMemberFeedbackReporting = None
      ),
      PolicyModule(
        policies = Empty,
        nPolicies = 0,
        totalPages = 0,
        selectedPage = 0,
        policyFeedbackReporting = None
      )
    )

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.userModule.users)((m, v) => m.copy(userModule = m.userModule.copy(users = v)))),
    new UserFeedbackHandler(zoomRW(_.userModule.feedbackReporting)((m, v) => m.copy(userModule = m.userModule.copy(feedbackReporting = v)))),
    new UserPagesAndTotalHandler(zoomRW(root => (root.userModule.nUsers, root.userModule.totalPages, root.userModule.selectedPage))((m, v) =>
      m.copy(userModule = m.userModule.copy(nUsers = v._1, totalPages = v._2, selectedPage = v._3)))),
    new UserGroupHandler(zoomRW(_.userModule.selectedUserGroups.userGroups)((m, v) =>
      m.copy(userModule = m.userModule.copy(selectedUserGroups = m.userModule.selectedUserGroups.copy(userGroups = v))))),
    new UserGroupsPagesAndTotalHandler(
      zoomRW(
        root =>
          (root.userModule.selectedUserGroups.nUserGroups,
           root.userModule.selectedUserGroups.totalPages,
           root.userModule.selectedUserGroups.selectedPage))((m, v) =>
        m.copy(userModule = m.userModule.copy(
          selectedUserGroups = m.userModule.selectedUserGroups.copy(nUserGroups = v._1, totalPages = v._2, selectedPage = v._3))))),
    new GroupHandler(zoomRW(_.groupModule.groups)((m, v) => m.copy(groupModule = m.groupModule.copy(groups = v)))),
    new GroupFeedbackHandler(
      zoomRW(_.groupModule.groupFeedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(groupFeedbackReporting = v)))),
    new GroupMemberHandler(zoomRW(_.groupModule.members)((m, v) => m.copy(groupModule = m.groupModule.copy(members = v)))),
    new GroupMemberFeedbackHandler(
      zoomRW(_.groupModule.groupMemberFeedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(groupMemberFeedbackReporting = v)))),
    new GroupPolicyHandler(zoomRW(_.groupModule.policies)((m, v) => m.copy(groupModule = m.groupModule.copy(policies = v)))),
    new PolicyHandler(zoomRW(_.policyModule.policies)((m, v) => m.copy(policyModule = m.policyModule.copy(policies = v)))),
    new PolicyPagesAndTotalHandler(
      zoomRW(root => (root.policyModule.nPolicies, root.policyModule.totalPages, root.policyModule.selectedPage))((m, v) =>
        m.copy(policyModule = m.policyModule.copy(nPolicies = v._1, totalPages = v._2, selectedPage = v._3)))),
    new PolicyFeedbackHandler(
      zoomRW(_.policyModule.policyFeedbackReporting)((m, v) => m.copy(policyModule = m.policyModule.copy(policyFeedbackReporting = v))))
  )
}
