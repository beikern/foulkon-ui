package client.appstate

import autowire._
import diode._
import diode.data._
import shared.entities.User
import client.services.AjaxClient
import shared.Api

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import boopickle.Default._
import diode.react.ReactConnector


// Actions
case object RefreshUsers extends Action
case class UpdateAllUsers(users: Seq[User]) extends Action

// Handlers
class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshUsers =>
      effectOnly(Effect(AjaxClient[Api].getUsers().call().map(UpdateAllUsers)))
    case UpdateAllUsers(users) =>
      updated(Ready(Users(users)))
  }
}

// The Root model for the application
case class RootModel(users: Pot[Users])

case class Users(users: Seq[User])

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  override protected def initialModel: RootModel = RootModel(Empty)

  override protected def actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.users)((m, v) => m.copy(users = v)))
  )
}
