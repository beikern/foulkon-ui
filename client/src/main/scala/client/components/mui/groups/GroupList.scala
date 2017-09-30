package client
package components.mui.groups

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import shared.FoulkonError
import shared.entities.GroupDetail

object GroupList {

  case class Props(
    groupsEither: Either[FoulkonError, List[GroupDetail]],
    updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
    deleteGroupCallback: (GroupOrg, GroupName) => Callback
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
                          p.updateGroupCallback,
                          p.deleteGroupCallback
                        ))
              )
              .toTagMod
          case Left(error) => <.div(error.toString) // TODO beikern arreglar, mostrar una card de error
        })
      }
    )
    .build

  def apply(groupsEither: Either[FoulkonError, List[GroupDetail]],
            updateGroupCallback: (GroupOrg, GroupName, GroupName, GroupPath) => Callback,
            deleteGroupCallback: (GroupOrg, GroupName) => Callback
           ) = GroupList(Props(groupsEither, updateGroupCallback, deleteGroupCallback))
}
