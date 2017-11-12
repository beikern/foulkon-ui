package client.appstate.users.groups

import client.appstate.UserGroups
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class UserGroupsComponentZoomedModel(
  userGroups: Pot[UserGroups],
  totalPages: TotalPages,
  selectedPage: SelectedPage
)
