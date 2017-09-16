package clients

import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import contexts.AkkaContext

import utils.AppConfig._

trait FoulkonConfig { self: AkkaContext =>
  implicit val sttpBackend = AkkaHttpBackend.usingActorSystem(actorSystem)

  val foulkonHost: String     = FoulkonConfig.foulkonHost
  val foulkonPort: String     = FoulkonConfig.foulkonPort.toString
  val foulkonUser: String     = FoulkonConfig.foulkonUser
  val foulkonPassword: String = FoulkonConfig.foulkonPassword
}
