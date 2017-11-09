package client.appstate.policies

import client.appstate.Policies
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class PolicyComponentZoomedModel(policies: Pot[Policies], totalPages: TotalPages, selectedPage: SelectedPage)
