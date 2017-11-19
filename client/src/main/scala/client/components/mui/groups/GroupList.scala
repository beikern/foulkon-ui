package client
package components.mui.groups

import client.routes.AppRouter.Location
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import shared.entities.GroupDetail

object GroupList {

  case class Props(
    groups: List[GroupDetail],
    router: RouterCtl[Location],
    updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroupCallback: (GroupOrg, GroupName) => Callback
  )

  private val GroupList = ScalaComponent
    .builder[Props]("GroupList")
    .render_P(
      p =>
        <.div(^.className := "card-nested-padded",
          p.groups.map(
            group =>
              <.div(
                ^.className := "card-padded",
                GroupCard(
                  group,
                  p.router,
                  p.updateGroupCallback,
                  p.deleteGroupCallback
                )
              )
          ).toTagMod
        )
      )
    .build

  def apply(groups: List[GroupDetail],
            router: RouterCtl[Location],
            updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
            deleteGroupCallback: (GroupOrg, GroupName) => Callback
           ) =
    GroupList(
      Props(groups, router, updateGroupCallback, deleteGroupCallback))
}