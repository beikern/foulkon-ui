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

import scala.collection.mutable
import scala.concurrent.Future

// Policies Actions
case object FetchPoliciesToReset                                                          extends Action
case class ResetPolicies(policies: Either[FoulkonError, (Total, List[PolicyDetail])])     extends Action
case class FetchPoliciesToConcat(request: ReadPoliciesRequest)                            extends Action
case class ConcatNewPolicies(policies: Either[FoulkonError, (Total, List[PolicyDetail])]) extends Action
case class CreatePolicy(request: CreatePolicyRequest)                                     extends Action
case class DeletePolicy(request: DeletePolicyRequest)                                     extends Action
case class UpdatePolicy(request: UpdatePolicyRequest)                                     extends Action
case class UpdatePolicyFeedbackReporting(feedback: Either[FoulkonError, MessageFeedback]) extends Action
case class UpdateTotalIncrementPolicyOffset(total: Total, updatedOffset: Offset)          extends Action
case class ResetTotalResetPolicyOffset(total: Total, Offset: Offset)                      extends Action

// Policies Handlers
class PolicyHandler[M](modelRW: ModelRW[M, Pot[Policies]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case FetchPoliciesToReset =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPolicies(ReadPoliciesRequest())
            .call
            .map(
              policiesDetailEither =>
                ResetPolicies(
                  policiesDetailEither
              )
            )
        )
      )
    case ResetPolicies(policies) =>
      policies match {
        case rightResult @ Right((total, fetchedPoliciesToReset)) =>
          val valueToUpdateState = rightResult.map {
            case (_, policyDetailList) =>
              val idPolicyTuple = policyDetailList.map {
                policyDetail =>
                  policyDetail.id -> policyDetail
              }
              mutable.LinkedHashMap(idPolicyTuple:_ *)
          }
          updated(
            Ready(
              Policies(valueToUpdateState)
            ),
            Effect(Future(ResetTotalResetPolicyOffset(total, fetchedPoliciesToReset.size)))
          )
        case leftResult @ Left(_) =>
          val valueToUpdateState = leftResult.map {
            case (_, policyDetailList) =>
              val idPolicyTuple = policyDetailList.map {
                policyDetail =>
                  policyDetail.id -> policyDetail
              }
              mutable.LinkedHashMap(idPolicyTuple:_ *)
          }
          updated(
            Ready(Policies(valueToUpdateState)),
            Effect(
              Future(ResetTotalResetPolicyOffset(0, 0))
            )
          )
      }
    case FetchPoliciesToConcat(request) =>
      effectOnly(
        Effect(
          AjaxClient[Api]
            .readPolicies(request)
            .call
            .map(
              policiesDetailEither =>
                ConcatNewPolicies(
                  policiesDetailEither
              )
            )
        )
      )
    case ConcatNewPolicies(policies) =>
      policies match {
        case rightResult @ Right((total, fetchedPolicies)) =>
          val valueToUpdateState = rightResult.map {
            case (_, policyDetailList) =>
              val idPolicyTuple = policyDetailList.map {
                policyDetail =>
                  policyDetail.id -> policyDetail
              }
              mutable.LinkedHashMap(idPolicyTuple:_ *)
          }

          if (modelRW.value.isEmpty) {
            updated(
              Ready(Policies(valueToUpdateState)),
              Effect(Future(UpdateTotalIncrementPolicyOffset(total, fetchedPolicies.size)))
            )
          } else {
            val concatResult = modelRW.value.map(
              policyWrapper =>
              for {
                statePolicies <- policyWrapper.policies
                value <- valueToUpdateState
              } yield {
                statePolicies ++= value
              }
            )
            updated(
              concatResult.map(Policies),
              Effect(Future(UpdateTotalIncrementPolicyOffset(total, fetchedPolicies.size)))
            )
          }
        case leftResult @ Left(_) =>
          val valueToUpdateState = leftResult.map {
            case (_, policyDetailList) =>
              val idPolicyTuple = policyDetailList.map {
                policyDetail =>
                  policyDetail.id -> policyDetail
              }
              mutable.LinkedHashMap(idPolicyTuple:_ *)
          }
          updated(
            Ready(Policies(valueToUpdateState)),
            Effect(
              Future(ResetTotalResetPolicyOffset(0, 0))
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
      updated(Some(PolicyFeedbackReporting(feedback)), Effect(Future(FetchPoliciesToReset)))
  }
}

class PolicyOffsetHandler[M](modelRW: ModelRW[M, (Total, Offset)]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case UpdateTotalIncrementPolicyOffset(total, offset) =>
      val (_, stateOffset) = modelRW.value
      updated(total -> (stateOffset + offset))

    case ResetTotalResetPolicyOffset(total, offset) =>
      updated(total -> offset)
  }
}
