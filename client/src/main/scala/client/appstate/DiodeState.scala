package client
package appstate

import client.appstate.groups.members.{GroupMemberFeedbackHandler, GroupMemberHandler, GroupMembersPagesAndTotalHandler}
import client.appstate.groups.policies.{GroupPoliciesPagesAndTotalHandler, GroupPolicyHandler}
import client.appstate.groups.{GroupFeedbackHandler, GroupHandler, GroupPagesAndTotalHandler}
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

// Users
case class UserFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class UserGroups(userGroups: Either[FoulkonError, List[UserGroup]])
case class UserGroupsModule(
    userGroups: Pot[UserGroups],
    nUserGroups: TotalUserGroups,
    totalPages: TotalPages,
    selectedPage: SelectedPage
)
case class Users(users: Either[FoulkonError, List[UserDetail]])
case class UserModule(
    users: Pot[Users],
    nUsers: TotalUsers,
    totalPages: TotalPages,
    selectedPage: SelectedPage,
    selectedUserGroups: UserGroupsModule,
    feedbackReporting: Option[UserFeedbackReporting]
)

// Groups
case class GroupFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class GroupMembers(groupMembers: Either[FoulkonError, List[MemberAssociatedToGroupInfo]])
case class GroupMembersModule(
  groupMembers: Pot[GroupMembers],
  nGroupMembers: TotalUserGroups,
  totalPages: TotalPages,
  selectedPage: SelectedPage,
  groupMemberFeedbackReporting: Option[GroupMemberFeedbackReporting]
)
case class GroupPolicies(groupPolicies: Either[FoulkonError, List[PoliciesAssociatedToGroupInfo]])
case class GroupPoliciesModule(
  groupPolicies: Pot[GroupPolicies],
  nGroupPolicies: TotalGroupPolicies,
  totalPages: TotalPages,
  selectedPage: SelectedPage
)
case class Groups(groups: Either[FoulkonError, List[GroupDetail]])
case class GroupMemberFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])
case class GroupModule(
  groups: Pot[Groups],
  nGroups: TotalGroups,
  totalPages: TotalPages,
  selectedPage: SelectedPage,
  selectedGroupMembers: GroupMembersModule,
  selectedGroupPolicies: GroupPoliciesModule,
  groupFeedbackReporting: Option[GroupFeedbackReporting]
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
        selectedUserGroups = UserGroupsModule(
          userGroups = Empty,
          nUserGroups = 0,
          totalPages = 0,
          selectedPage = 0
        ),
        feedbackReporting = None
      ),
      GroupModule(
        groups = Empty,
        nGroups = 0,
        totalPages = 0,
        selectedPage = 0,
        selectedGroupMembers = GroupMembersModule(
          groupMembers = Empty,
          nGroupMembers = 0,
          totalPages = 0,
          selectedPage = 0,
          groupMemberFeedbackReporting = None
        ),
        selectedGroupPolicies = GroupPoliciesModule(
          groupPolicies = Empty,
          nGroupPolicies = 0,
          totalPages = 0,
          selectedPage = 0
        ),
        groupFeedbackReporting = None
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
    new GroupPagesAndTotalHandler(zoomRW(root => (root.groupModule.nGroups, root.groupModule.totalPages, root.groupModule.selectedPage))((m, v) =>
      m.copy(groupModule = m.groupModule.copy(nGroups = v._1, totalPages = v._2, selectedPage = v._3)))),
    new GroupMemberHandler(zoomRW(_.groupModule.selectedGroupMembers.groupMembers)((m, v) => m.copy(groupModule = m.groupModule.copy(selectedGroupMembers = m.groupModule.selectedGroupMembers.copy(groupMembers = v))))),
    new GroupMembersPagesAndTotalHandler(
      zoomRW(
        root =>
          (root.groupModule.selectedGroupMembers.nGroupMembers,
            root.groupModule.selectedGroupMembers.totalPages,
            root.groupModule.selectedGroupMembers.selectedPage))((m, v) =>
        m.copy(groupModule = m.groupModule.copy(
          selectedGroupMembers = m.groupModule.selectedGroupMembers.copy(nGroupMembers = v._1, totalPages = v._2, selectedPage = v._3))))
    ),
    new GroupMemberFeedbackHandler(
      zoomRW(_.groupModule.selectedGroupMembers.groupMemberFeedbackReporting)((m, v) => m.copy(groupModule = m.groupModule.copy(selectedGroupMembers = m.groupModule.selectedGroupMembers.copy(groupMemberFeedbackReporting = v))))),
    new GroupPolicyHandler(zoomRW(_.groupModule.selectedGroupPolicies.groupPolicies)((m, v) => m.copy(groupModule = m.groupModule.copy(selectedGroupPolicies = m.groupModule.selectedGroupPolicies.copy(groupPolicies = v))))),
    new GroupPoliciesPagesAndTotalHandler(
      zoomRW(
        root =>
          (root.groupModule.selectedGroupPolicies.nGroupPolicies,
            root.groupModule.selectedGroupPolicies.totalPages,
            root.groupModule.selectedGroupPolicies.selectedPage))((m, v) =>
        m.copy(groupModule = m.groupModule.copy(
          selectedGroupPolicies = m.groupModule.selectedGroupPolicies.copy(nGroupPolicies = v._1, totalPages = v._2, selectedPage = v._3))))
    ),
    new PolicyHandler(zoomRW(_.policyModule.policies)((m, v) => m.copy(policyModule = m.policyModule.copy(policies = v)))),
    new PolicyPagesAndTotalHandler(
      zoomRW(root => (root.policyModule.nPolicies, root.policyModule.totalPages, root.policyModule.selectedPage))((m, v) =>
        m.copy(policyModule = m.policyModule.copy(nPolicies = v._1, totalPages = v._2, selectedPage = v._3)))),
    new PolicyFeedbackHandler(
      zoomRW(_.policyModule.policyFeedbackReporting)((m, v) => m.copy(policyModule = m.policyModule.copy(policyFeedbackReporting = v))))
  )
}
