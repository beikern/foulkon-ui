package client.routes

import java.util.UUID

import chandu0101.scalajs.react.components.materialui.MuiMuiThemeProvider
import client.appstate.SPACircuit
import client.components.mui.groups.{GroupFeedbackSnackbar, GroupTabs}
import client.components.mui.users.{UserFeedbackSnackbar, UsersComponent}
import client.components.mui.{CountAndFilterToolBar, NavToolBar}
import japgolly.scalajs.react.extra.router.StaticDsl.Route
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

object AppRouter {

  case class GroupUUID (id: UUID)

  sealed trait Location

  case object UsersLocation          extends Location
  case object GroupsLocation         extends Location
  case object PoliciesLocation       extends Location
  case object ProxyResourcesLocation extends Location
  case object OidcProviderLocation   extends Location
  case object AuthoritationLocation  extends Location
  case class GroupMembersLocation(id: UUID) extends Location

  // Configure the router
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location]
    .buildConfig { dsl =>
      import dsl._

      val userWrapper         = SPACircuit.connect(_.userModule.users)
      val userFeedbackWrapper = SPACircuit.connect(_.userModule.feedbackReporting)

      val groupWrapper         = SPACircuit.connect(_.groupModule)
      val groupFeedbackWrapper = SPACircuit.connect(_.groupModule.feedbackReporting)

      val usersRoute: Rule =
        staticRoute("#users", UsersLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Users", 1))),
                MuiMuiThemeProvider()(userWrapper(UsersComponent(_))),
                MuiMuiThemeProvider()(userFeedbackWrapper(UserFeedbackSnackbar(_)))
            )
          )

      val groupsRoute: Rule =
        staticRoute("#groups", GroupsLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Groups", 1))),
                MuiMuiThemeProvider()(groupWrapper(GroupTabs(_))),
                MuiMuiThemeProvider()(groupFeedbackWrapper(GroupFeedbackSnackbar(_)))
            )
          )

      val policiesRoute: Rule =
        staticRoute("#policies", PoliciesLocation) ~>
          renderR(
            ctl =>
              <.div(
                MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Policies", 1)))
            )
          )

//      val test = dynamicRouteCT("groups" / uuid.caseClass[GroupMembersLocation] / "members")

      (usersRoute
          | groupsRoute
          | policiesRoute
      ).notFound(redirectToPage(UsersLocation)(Redirect.Replace))
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
