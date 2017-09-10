package client.routes

import chandu0101.scalajs.react.components.materialui.MuiMuiThemeProvider
import client.appstate.SPACircuit
import client.components.mui.users.UsersComponent
import client.components.mui.{CountAndFilterToolBar, NavToolBar}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

object AppRouter {

  sealed trait Location

  case object UsersLocation extends Location
  case object GroupsLocation extends Location
  case object PoliciesLocation extends Location
  case object ProxyResourcesLocation extends Location
  case object OidcProviderLocation extends Location
  case object AuthoritationLocation extends Location

  // Configure the router
  val routerConfig: RouterConfig[Location] = RouterConfigDsl[Location].buildConfig { dsl =>
    import dsl._

    val userWrapper = SPACircuit.connect(_.users)

    val usersRoute: Rule =
      staticRoute("#users", UsersLocation) ~>
        renderR(
          ctl =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Users", 1))),
                MuiMuiThemeProvider()
                (userWrapper(UsersComponent(_)))
            )
        )

    val groupsRoute: Rule =
      staticRoute("#groups", GroupsLocation) ~>
        renderR(
          ctl =>
            <.div(
              MuiMuiThemeProvider()(CountAndFilterToolBar(CountAndFilterToolBar.Props("Groups", 1)))
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

    (usersRoute
      | groupsRoute
      | policiesRoute).notFound(redirectToPage(UsersLocation)(Redirect.Replace))
  }.renderWith(layout)

  def layout(c: RouterCtl[Location], r: Resolution[Location]) = {
    val appBar = MuiMuiThemeProvider()(NavToolBar(c, "Foulkon UI"))

    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar, navbar-fixed-top, zero-margin-bottom",
        <.div(^.className := "container", appBar)
      )
      ,
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }
}
