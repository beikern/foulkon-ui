package client.components.mui.groups.members

import chandu0101.scalajs.react.components.materialui.{Mui, MuiCard, MuiCardHeader, MuiCardText, MuiFloatingActionButton}
import client.appstate._
import client.routes.AppRouter.Location
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._

import scala.scalajs.js
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._
import scalacss.internal.mutable.StyleSheet

object MembersComponent {

  object Style extends StyleSheet.Inline {
    import dsl._
    val createGroupMemberButton = style(
      float.right
    )
  }

  case class Props(id: String, proxy: ModelProxy[Map[String, GroupMetadataWithMember]], router: RouterCtl[Location])
  case class State(createGroupMemberAssocDialogOpened: Boolean = false)

  class Backend($ : BackendScope[Props, State]) {

    val changeCreateGroupMemberAssocDialogStateCallback = (dialogState: Boolean) => {
      $.modState(s => s.copy(createGroupMemberAssocDialogOpened = dialogState))
    }

    def showCreateGroupMemberDialogCallback(event: ReactEvent): Callback = {
      $.modState(s => s.copy(createGroupMemberAssocDialogOpened = true))
    }

    def mounted(props: Props) =
      Callback.empty

    def render(p: Props, s: State) = {
      val memberInfo = p.proxy.value.get(p.id)
      <.div(
        memberInfo match {
          case Some(GroupMetadataWithMember(organizationId, name, Right(List()))) =>
            println(s"SOME CASE, LIST EMPTY")
            <.div(
              CreateMemberGroupDialog(
                p.id,
                organizationId,
                name,
                s.createGroupMemberAssocDialogOpened,
                changeCreateGroupMemberAssocDialogStateCallback,
                (id, org, name, userid) => p.proxy.dispatchCB(AddGroupMember(id, org, name, userid))
              ),
              <.div(
                ^.className := "card-padded",
                MuiCard()(
                  MuiCardHeader(
                    title = <.span(<.b(s"Group ${p.id} members")).render
                  )(),
                  MuiCardText()(<.div("This group does not have members. Sorry!"))
                )
              )
            )
          case Some(GroupMetadataWithMember(_, _, Left(foulkonError))) =>
            println(s"SOME CASE, foulkon error")
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Group ${p.id} members")).render
                )(),
                MuiCardText()(<.div(s"Can't list group members. Foulkon code: ${foulkonError.code} .Foulkon error:${foulkonError.message} . Sorry!"))
              )
            )
          case Some(GroupMetadataWithMember(organizationId, name, Right(memberInfoList))) =>
            println(s"SOME CASE, list not empty")
            <.div(
              CreateMemberGroupDialog(
                p.id,
                organizationId,
                name,
                s.createGroupMemberAssocDialogOpened,
                changeCreateGroupMemberAssocDialogStateCallback,
                (id, org, name, userid) => p.proxy.dispatchCB(AddGroupMember(id, org, name, userid))
              ),
              <.div(
                ^.className := "card-padded",
                MuiCard()(
                  MuiCardHeader(
                    title = <.span(<.b(s"Group ${p.id} members")).render
                  )(),
                  MemberList(p.id,
                             organizationId,
                             name,
                             memberInfoList,
                             (id, org, name, userid) => p.proxy.dispatchCB(RemoveGroupMember(id, org, name, userid)))
                )
              )
            )
          case None =>
            println("NONE CASE")
            <.div(
              ^.className := "card-padded",
              MuiCard()(
                MuiCardHeader(
                  title = <.span(<.b(s"Group ${p.id} members")).render
                )(),
                MuiCardText()(<.div("This group is not in the model. Sorry!"))
              )
            )
        },
        <.div(
          Style.createGroupMemberButton,
          MuiFloatingActionButton(onTouchTap = js.defined(showCreateGroupMemberDialogCallback _))(
            Mui.SvgIcons.ContentAdd.apply(style = js.Dynamic.literal(width = "30px", height = "30px"))()
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("GroupMembersComponent")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(
      id: String,
      proxy: ModelProxy[Map[String, GroupMetadataWithMember]],
      router: RouterCtl[Location]
  ) = component(Props(id, proxy, router))

}
