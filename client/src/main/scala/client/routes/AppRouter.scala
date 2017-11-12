package client.routes

import java.util.UUID

import chandu0101.scalajs.react.components.materialui.MuiMuiThemeProvider
import client.appstate.SPACircuit
import client.appstate.policies.PolicyComponentZoomedModel
import client.appstate.users.UserComponentZoomedModel
import client.appstate.users.groups.UserGroupsComponentZoomedModel
import client.components.mui.groups.members.{GroupMemberFeedbackSnackbar, MembersComponent}
import client.components.mui.groups.policies.GroupPoliciesComponent
import client.components.mui.groups.{GroupFeedbackSnackbar, GroupsComponent}
import client.components.mui.policies.statements.PolicyStatementsComponent
import client.components.mui.policies.{PoliciesComponent, PolicyFeedbackSnackbar}
import client.components.mui.users.{UserFeedbackSnackbar, UsersComponent}
import client.components.mui.{CountAndFilterToolBar, NavToolBar}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import client.components.mui.users.groups.UserGroupsComponent
object AppRouter {

  case class GroupUUID(id: UUID)

  sealed trait Location

  case object UsersLocation                     extends Location
  case class UserGroupsLocation(userId: UUID, userExternalId: String) extends Location
  case class UserGroupsExternalIdLocation(externalId: String) extends Location
  case object GroupsLocation                    extends Location
  case object PoliciesLocation                  extends Location
  case object ProxyResourcesLocation            extends Location
  case object OidcProviderLocation              extends Location
  case object AuthoritationLocation             extends Location
  case class GroupMembersLocation(id: UUID)     extends Location
  case class GroupPoliciesLocation(id: UUID)    extends Location
  case class PolicyStatementsLocation(id: UUID) extends Location
  // Configure the router
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location]
    .buildConfig { dsl =>
      import dsl._

      val userWrapper = SPACircuit.connect(
        rootModel =>
          UserComponentZoomedModel(
            rootModel.userModule.users,
            rootModel.userModule.totalPages,
            rootModel.userModule.selectedPage
        )
      )

      val userGroupWrapper = SPACircuit.connect(
        rootModel =>
          UserGroupsComponentZoomedModel(
            rootModel.userModule.selectedUserGroups.userGroups,
            rootModel.userModule.selectedUserGroups.totalPages,
            rootModel.userModule.selectedUserGroups.selectedPage
          )
      )

      val userFeedbackWrapper = SPACircuit.connect(_.userModule.feedbackReporting)

      val groupWrapper         = SPACircuit.connect(_.groupModule)
      val groupFeedbackWrapper = SPACircuit.connect(_.groupModule.groupFeedbackReporting)

      val groupMemberWrapper         = SPACircuit.connect(_.groupModule.members)
      val groupMemberFeedbackWrapper = SPACircuit.connect(_.groupModule.groupMemberFeedbackReporting)

      val groupPolicyWrapper = SPACircuit.connect(_.groupModule.policies)

      val policyModuleWrapper = SPACircuit.connect(
        rootModel =>
          PolicyComponentZoomedModel(
            rootModel.policyModule.policies,
            rootModel.policyModule.totalPages,
            rootModel.policyModule.selectedPage
        )
      )

      val policiesWrapper = SPACircuit.connect(_.policyModule.policies)

      val policyFeedbackWrapper = SPACircuit.connect(_.policyModule.policyFeedbackReporting)

      // USERS
      val usersRoute: Rule =
        staticRoute("#users", UsersLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Users", 1))),
                MuiMuiThemeProvider()(userWrapper(UsersComponent(_, ctl))),
                MuiMuiThemeProvider()(userFeedbackWrapper(UserFeedbackSnackbar(_)))
            )
          )

      val userGroupsRoute: Rule = dynamicRouteCT(("#users" / uuid / "externalid" / string("[\\w+.@=\\-_]+") /"groups").caseClass[UserGroupsLocation]) ~>
        dynRenderR(
          (p: UserGroupsLocation, ctl) =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("User groups", 1))),
              MuiMuiThemeProvider()(userGroupWrapper(UserGroupsComponent(p.userId.toString, p.userExternalId, _, ctl)))
            )
        )

      // GROUPS
      val groupsRoute: Rule =
        staticRoute("#groups", GroupsLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Groups", 1))),
                MuiMuiThemeProvider()(groupWrapper(groupModule => GroupsComponent(groupModule.zoom(_.groups), ctl))),
                MuiMuiThemeProvider()(groupFeedbackWrapper(GroupFeedbackSnackbar(_)))
            )
          )

      val groupMembersRoute: Rule = dynamicRouteCT("#groups" / uuid.caseClass[GroupMembersLocation] / "members") ~>
        dynRenderR(
          (p: GroupMembersLocation, ctl) =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Group members", 1))),
              MuiMuiThemeProvider()(groupMemberWrapper(groupMetadataWithMember => MembersComponent(p.id.toString, groupMetadataWithMember, ctl))),
              MuiMuiThemeProvider()(groupMemberFeedbackWrapper(GroupMemberFeedbackSnackbar(_)))
          )
        )

      val groupPoliciesRoute: Rule = dynamicRouteCT("#groups" / uuid.caseClass[GroupPoliciesLocation] / "policies") ~>
        dynRenderR(
          (p: GroupPoliciesLocation, ctl) =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Group policies", 1))),
              MuiMuiThemeProvider()(
                groupPolicyWrapper(groupMetadataWithPolicy => GroupPoliciesComponent(p.id.toString, groupMetadataWithPolicy, ctl)))
          )
        )

      // POLICIES
      val policiesRoute: Rule =
        staticRoute("#policies", PoliciesLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Policies", 1))),
                MuiMuiThemeProvider()(policyModuleWrapper(PoliciesComponent(_, ctl))),
                MuiMuiThemeProvider()(policyFeedbackWrapper(PolicyFeedbackSnackbar(_)))
            )
          )

      val policyStatementsRoute: Rule = dynamicRouteCT("#policies" / uuid.caseClass[PolicyStatementsLocation] / "statements") ~>
        dynRenderR(
          (p: PolicyStatementsLocation, ctl) =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Policy statements", 1))),
              MuiMuiThemeProvider()(policiesWrapper(policyProxy => PolicyStatementsComponent(p.id.toString, policyProxy, ctl)))
          )
        )

      (emptyRule
        | usersRoute
        | userGroupsRoute
        | groupsRoute
        | groupMembersRoute
        | groupPoliciesRoute
        | policiesRoute
        | policyStatementsRoute).notFound(redirectToPage(UsersLocation)(Redirect.Replace))
    }
    .renderWith(layout)

  def layout(c: RouterCtl[Location], r: Resolution[Location]) = {
    val appBar = MuiMuiThemeProvider()(NavToolBar(c, "Foulkon UI"))

    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar, navbar-fixed-top, zero-margin-bottom", <.div(^.className := "container", appBar)),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }
}
