package client.appstate.groups.policies

import autowire._
import client.appstate.GroupPolicies
import client.services.AjaxClient
import diode.data._
import diode.{Effect, _}
import shared._
import shared.requests.groups.policies._
import boopickle.Default._
import shared.responses.groups.policies.PoliciesAssociatedToGroupInfo
import shared.utils.constants._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Future

//Actions
// Group policies

case object ResetGroupPolicies extends Action
case class FetchGroupPolicies(request: PoliciesAssociatedToGroupRequest) extends Action
case class SetGroupPolicies(groupPolicies: Either[FoulkonError, (TotalGroupPolicies, List[PoliciesAssociatedToGroupInfo])]) extends Action
case class UpdateTotalGroupPoliciesAndPages(totalGroupPolicies: TotalGroupPolicies) extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage) extends Action

class GroupPolicyHandler[M](modelRW: ModelRW[M, Pot[GroupPolicies]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case ResetGroupPolicies =>
      updated(
        Empty,
        Effect(Future(UpdateTotalGroupPoliciesAndPages(0)))
          >> Effect(Future(UpdateSelectedPage(0)))
      )
    case FetchGroupPolicies(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api].readPoliciesAssociatedToGroup(request)
          .call
          .map(SetGroupPolicies)
        )
      )
    case SetGroupPolicies(groupPolicies) =>
      groupPolicies match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(GroupPolicies(rightResult.map(_._2))),
            Effect(Future(UpdateTotalGroupPoliciesAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(GroupPolicies(leftResult.map(_._2))),
            Effect(Future(UpdateTotalGroupPoliciesAndPages(0))) >> Effect(Future(UpdateSelectedPage(0)))
          )
      }
  }
}

class GroupPoliciesPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalGroupPolicies, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalGroupPoliciesAndPages(totalGroupPolicies) =>
      val totalPages        = (totalGroupPolicies.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalGroupPolicies, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}
