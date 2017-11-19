package client.appstate.groups

import client.appstate.Groups
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class GroupComponentZoomedModel (
  groups: Pot[Groups],
  totalPages: TotalPages,
  selectedPage: SelectedPage
)
