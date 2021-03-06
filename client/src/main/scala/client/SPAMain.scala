package client

import client.components.GlobalStyles
import client.components.internal.ReactTapEventPlugin
import client.components.mui.groups.members.{MemberCard, GroupMembersComponent}
import client.components.mui.groups.policies.{GroupPoliciesComponent, GroupPolicyCard}
import client.components.mui.groups.{GroupCard, GroupsComponent}
import client.components.mui.policies.{PoliciesComponent, PolicyCard, StatementCard}
import client.components.mui.users.{UserCard, UsersComponent}
import japgolly.scalajs.react.extra.router._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.GlobalRegistry

@JSExportTopLevel("SPAMain")
object SPAMain extends JSApp {

  ReactTapEventPlugin(js.undefined)

  @JSExport
  def main(): Unit = {
    import client.routes.AppRouter._

    // create stylesheet
    GlobalRegistry.register(UserCard.Style) // TODO beikern: move to a loader method like scala-react-components does.
    GlobalRegistry.register(UsersComponent.Style)

    GlobalRegistry.register(GroupCard.Style)
    GlobalRegistry.register(GroupsComponent.Style)

    GlobalRegistry.register(MemberCard.Style)
    GlobalRegistry.register(GroupMembersComponent.Style)

    GlobalRegistry.register(GroupPolicyCard.Style)
    GlobalRegistry.register(GroupPoliciesComponent.Style)

    GlobalRegistry.register(PoliciesComponent.Style)
    GlobalRegistry.register(PolicyCard.Style)
    GlobalRegistry.register(StatementCard.Style)

    GlobalRegistry.addToDocumentOnRegistration()

    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)

    // tell React to render the router in the document body
    router.mapUnmounted(_.renderIntoDOM(dom.document.getElementById("root")))
  }
}
