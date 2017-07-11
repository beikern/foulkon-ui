package client.components.own

import client.components.bootstrap.pagelayouts.lists.{ListGroup, ListGroupItem}
import client.components.bootstrap.pagelayouts.panels.Panel
import client.components.bootstrap.styles.BsStyle
import shared.entities.User
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object UserList {
  case class UserListProps(
      users: Seq[User]
  )

  private val UserList = ScalaComponent.builder[UserListProps]("UserList")
  .render_P(
    p => <.div(^.className := "so-padded",
      {
      val userMap: Map[String, Seq[User]] = p.users.groupBy(_.externalId.headOption.map(_.toString).getOrElse("Miscel"))

      userMap.keySet.toList.sortWith(_ < _).map{
        keyLetter =>
          val listItems = userMap(keyLetter).map {
            user =>
              ListGroupItem(ListGroupItem.Props(), user.externalId).vdomElement
          }
          val listGroup = ListGroup(ListGroup.Props(fill = Some(true)), listItems:_ *)
          Panel(
          Panel.Props(
          header = Some(keyLetter),
          bsStyle = BsStyle.primary,
          collapsible = Some(true)
          ),
          listGroup)
      }
    }.toTagMod(<.div(_)))
  ).build

  def apply(users: Seq[User]) = UserList(UserListProps(users))
}

