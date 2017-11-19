package client.appstate.groups.members

import client.appstate.GroupMembers
import diode.data.Pot
import shared.{SelectedPage, TotalPages}

case class GroupMemberComponentZoomedModel(
    groupMembers: Pot[GroupMembers],
    totalPages: TotalPages,
    selectedPage: SelectedPage
)
