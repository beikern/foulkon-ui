package client
package components.mui.groups

import client.routes.AppRouter.Location
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.FoulkonError
import shared.entities.GroupDetail

object GroupList {

  case class Props(
    groupsEither: Either[FoulkonError, List[GroupDetail]],
    router: RouterCtl[Location],
    updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroupCallback: (GroupOrg, GroupName) => Callback,
    retrieveGroupMemberInfoCallback: (String, GroupOrg, GroupName) => Callback
  )

  private val GroupList = ScalaComponent
    .builder[Props]("GroupList")
    .render_P(
      p => {
        <.div(p.groupsEither match {
          case Right(groups) =>
            groups
              .map(
                group =>
                  <.div(^.className := "card-padded",
                        GroupCard(
                          group,
                          p.router,
                          p.updateGroupCallback,
                          p.deleteGroupCallback,
                          p.retrieveGroupMemberInfoCallback
                        ))
              )
              .toTagMod
          case Left(error) => <.div(error.toString) // TODO beikern arreglar, mostrar una card de error
        })
      }
    )
    .build

  def apply(groupsEither: Either[FoulkonError, List[GroupDetail]],
            router: RouterCtl[Location],
            updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
            deleteGroupCallback: (GroupOrg, GroupName) => Callback,
            retrieveGroupMemberInfoCallback: (String, GroupOrg, GroupName) => Callback
           ) = GroupList(Props(groupsEither, router, updateGroupCallback, deleteGroupCallback, retrieveGroupMemberInfoCallback))
}
