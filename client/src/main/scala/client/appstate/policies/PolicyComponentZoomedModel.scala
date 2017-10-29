package client.appstate.policies

import client.appstate.Policies
import diode.data.Pot
import shared.{Offset, Total}

case class PolicyComponentZoomedModel(policies: Pot[Policies], total: Total, offset: Offset)
