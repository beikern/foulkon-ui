package client.components.mui.groups.policies

import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate._
import client.routes.AppRouter.Location
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object GroupPoliciesComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createGroupPolicyButton = style(
      float.right
    )
  }

  case class Props(id: String, proxy: ModelProxy[Map[String, GroupMetadataWithPolicy]], router: RouterCtl[Location])
  case class State(createGroupPolicyAssocDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {

    val changeCreateGroupPolicyAssocDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupPolicyAssocDialogOpened = dialogState))
    }

    def showCreateGroupPolicyDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupPolicyAssocDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.empty

    def render(p: Props, s: State) = {
      val memberInfo = p.proxy.value.get(p.id)
      <.div(
        memberInfo match {
          case Some(GroupMetadataWithPolicy(organizationId, name, Right(List()))) =>
            <.div(
              <.div(
                ^.className := "card-padded",
                MuiCard()(
                  MuiCardHeader(
                    title = <.span(<.b(s"Group ${p.id} policies")).render
                  )(),
                  MuiCardText()(<.div("This group does not have policies attached. Sorry!"))
                )
              )
            )
          case Some(GroupMetadataWithPolicy(_, _, Left(foulkonError))) =>
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Group ${p.id} policies")).render
                )(),
                MuiCardText()(<.div(s"Can't list group policies. Foulkon code: ${foulkonError.code} .Foulkon error:${foulkonError.message} . Sorry!"))
              )
            )
          case Some(GroupMetadataWithPolicy(organizationId, name, Right(policyInfoList))) =>
            <.div(
              <.div(
                ^.className := "card-padded",
                MuiCard()(
                  MuiCardHeader(
                    title = <.span(<.b(s"Group ${p.id} policies")).render
                  )(),
                  GroupPolicyList(
                    p.id,
                    organizationId,
                    name,
                    policyInfoList
                  )
                )
              )
            )
          case None =>
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Group ${p.id} policies")).render
                )(),
                MuiCardText()(<.div("This group is not in the model. Sorry!"))
              )
            )
        },
        <.div(
          Style.createGroupPolicyButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateGroupPolicyDialogCallback _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupPoliciesComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(
      id: String,
      proxy: ModelProxy[Map[String, GroupMetadataWithPolicy]],
      router: RouterCtl[Location]
  ) = component(Props(id, proxy, router))

}
