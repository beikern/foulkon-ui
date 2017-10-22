package contexts

import akka.actor.ActorSystem

trait AkkaContext {
  implicit val actorSystem: ActorSystem
  implicit val dispatcher = actorSystem.dispatcher
}
