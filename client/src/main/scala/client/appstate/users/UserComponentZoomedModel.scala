package client.appstate.users

import client.appstate.Users
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class UserComponentZoomedModel (
  users: Pot[Users],
  totalPages: TotalPages,
  selectedPage: SelectedPage
)