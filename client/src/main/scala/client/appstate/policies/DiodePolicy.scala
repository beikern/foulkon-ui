package client.appstate.policies

import autowire._
import diode._
import diode.data._
import shared.entities.PolicyDetail
import shared.{Api, FoulkonError, Offset, Total}
import client.services.AjaxClient

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import client.MessageFeedback
import client.appstate.{Policies, PolicyFeedbackReporting}
import shared.requests.policies.{CreatePolicyRequest, DeletePolicyRequest, ReadPoliciesRequest, UpdatePolicyRequest}
import shared.responses.policies.DeletePolicyResponse

import scala.concurrent.Future

// Policies Actions
case class RefreshPolicies(request: ReadPoliciesRequest)                                  extends Action
case class AddNewPolicies(policies: Either[FoulkonError, (Total, List[PolicyDetail])])    extends Action
case class CreatePolicy(request: CreatePolicyRequest)                                     extends Action
case class DeletePolicy(request: DeletePolicyRequest)                                     extends Action
case class UpdatePolicy(request: UpdatePolicyRequest)                                     extends Action
case class UpdatePolicyFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback]) extends Action
case class UpdatePolicyOffset(updatedOffset: Offset)                                      extends Action

// Policies Handlers
class PolicyHandler[M](modelRW: ModelRW[M, Pot[Policies]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshPolicies(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPolicies(request)
            .call
            .map(
              policiesDetailEither =>
                AddNewPolicies(
                  policiesDetailEither
              )
            )
        )
      )
    case AddNewPolicies(policies) =>
      policies match {
        case Right((total, addNewPoliciesList)) =>
          if (modelRW.value.isEmpty) {
            updated(
              Ready(Policies(policies)), Effect(Future(UpdatePolicyOffset(addNewPoliciesList.size)))
            )
          } else {
            val concatResult = modelRW.value.map(
              _.policies.map {
                case (_, statePolicyList) =>
                  total -> (statePolicyList ::: addNewPoliciesList)
              }
            )
            println(UpdatePolicyOffset(addNewPoliciesList.size))
            updated(
              concatResult.map(Policies),
              Effect(Future(UpdatePolicyOffset(addNewPoliciesList.size)))
            )
          }
        case error @ Left(foulkonError) =>
          updated(
            Ready(Policies(error)),
            Effect(
              Future(UpdatePolicyOffset(0))
            )
          )

      }

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
      updated(Some(PolicyFeedbackReporting(feedback)) /*, Effect(Future(RefreshPolicies))*/ )
  }
}

class PolicyOffsetHandler[M](modelRW: ModelRW[M, Offset]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdatePolicyOffset(offset) =>
      if(offset == 0) {
        updated(offset)
      } else {
        println("policyoffsethandler" + (modelRW.value + offset))
        updated(modelRW.value + offset)
      }
  }
}
