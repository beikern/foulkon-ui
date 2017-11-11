package client.appstate.policies

import autowire._
import diode._
import diode.data._
import shared.entities.PolicyDetail
import shared._
import client.services.AjaxClient

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import client.Constants.PageSize
import client.MessageFeedback
import client.appstate.{Policies, PolicyFeedbackReporting}
import shared.requests.policies.{CreatePolicyRequest, DeletePolicyRequest, ReadPoliciesRequest, UpdatePolicyRequest}
import shared.responses.policies.DeletePolicyResponse

import scala.concurrent.Future

// Policies Actions
object FetchPoliciesToReset                                                                 extends Action
case class SetPolicies(policies: Either[FoulkonError, (TotalPolicies, List[PolicyDetail])]) extends Action
case class FetchPolicies(request: ReadPoliciesRequest)                                      extends Action
case class CreatePolicy(request: CreatePolicyRequest)                                       extends Action
case class DeletePolicy(request: DeletePolicyRequest)                                       extends Action
case class UpdatePolicy(request: UpdatePolicyRequest)                                       extends Action
case class UpdatePolicyFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback])   extends Action
case class UpdateTotalPoliciesAndPages(totalPolicies: TotalPolicies)                        extends Action
case class UpdateSelectedPage(selectedPage: SelectedPage)                                   extends Action

// Policies Handlers
class PolicyHandler[M](modelRW: ModelRW[M, Pot[Policies]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case FetchPoliciesToReset =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPolicies(ReadPoliciesRequest(limit = PageSize))
            .call
            .map(SetPolicies)
        )
      )
    case SetPolicies(policies) =>
      policies match {
        case rightResult @ Right((total, _)) =>
          updated(
            Ready(Policies(rightResult.map(_._2))),
            Effect(Future(UpdateTotalPoliciesAndPages(total)))
          )
        case leftResult @ Left(_) =>
          updated(
            Ready(Policies(leftResult.map(_._2))),
            Effect(
              Future(UpdateTotalPoliciesAndPages(0))
            )
          )
      }
    case FetchPolicies(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPolicies(request)
            .call
            .map(SetPolicies)
        )
      )

    case CreatePolicy(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .createPolicy(request)
            .call
            .map {
              case Left(foulkonError)  => UpdatePolicyFeedbackReporting(Left(foulkonError))
              case Right(policyDetail) => UpdatePolicyFeedbackReporting(Right(s"policy ${policyDetail.name} created successfully!"))
            }
        )
      )
    case DeletePolicy(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .deletePolicy(request)
            .call
            .map {
              case Left(foulkonError)                    => UpdatePolicyFeedbackReporting(Left(foulkonError))
              case Right(DeletePolicyResponse(org, nam)) => UpdatePolicyFeedbackReporting(Right(s"Policy $nam with org $org deleted successfully!"))
            }
        )
      )
    case UpdatePolicy(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .updatePolicy(request)
            .call
            .map {
              case Left(foulkonError) => UpdatePolicyFeedbackReporting(Left(foulkonError))
              case Right(_)           => UpdatePolicyFeedbackReporting(Right(s"policy updated successfully!"))
            }
        )
      )
  }
}

class PolicyFeedbackHandler[M](modelRW: ModelRW[M, Option[PolicyFeedbackReporting]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdatePolicyFeedbackReporting(feedback) =>
      updated(Some(PolicyFeedbackReporting(feedback)), Effect(Future(FetchPoliciesToReset)))
  }
}

class PolicyPagesAndTotalHandler[M](modelRW: ModelRW[M, (TotalPolicies, TotalPages, SelectedPage)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalPoliciesAndPages(totalPolicies) =>
      val totalPages        = (totalPolicies.toFloat / PageSize.toFloat).ceil.toInt
      val stateSelectedPage = modelRW()._3
      updated((totalPolicies, totalPages, stateSelectedPage))
    case UpdateSelectedPage(selectedPage) =>
      updated(modelRW().copy(_3 = selectedPage))
  }
}
