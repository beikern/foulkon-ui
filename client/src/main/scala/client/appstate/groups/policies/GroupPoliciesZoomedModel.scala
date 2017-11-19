package client.appstate.groups.policies
import client.appstate.GroupPolicies
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class GroupPoliciesZoomedModel(
    groupsPolicies: Pot[GroupPolicies],
    totalPages: TotalPages,
    selectedPage: SelectedPage
)
