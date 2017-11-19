package controllers

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import boopickle.Default.{Pickle, Pickler, Unpickle}
import play.api.{Configuration, Environment}
import play.api.mvc.{Action, InjectedController, RawBuffer}
import services.{ApiService, ApiServiceMock}
import boopickle.Default._
import com.google.inject.Inject
import contexts.AkkaContext
import shared.Api

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R)         = Pickle.intoBytes(r)
}

object TwirlTemplate {
  def libraryUrl(projectName: String): Option[String] = {
    val name = projectName.toLowerCase
    Seq(s"$name-opt-library.js", s"$name-fastopt-library.js")
      .find(name => getClass.getResource(s"/public/$name") != null)
      .map(controllers.routes.Assets.versioned(_).url)
  }
  def loaderUrl(projectName: String): Option[String] = {
    val name = projectName.toLowerCase
    Seq(s"$name-opt-loader.js", s"$name-fastopt-loader.js")
      .find(name => getClass.getResource(s"/public/$name") != null)
      .map(controllers.routes.Assets.versioned(_).url)
  }
  def foulkonUIappUrl(projectName: String): Option[String] = {
    val name = projectName.toLowerCase
    Seq(s"$name-opt.js", s"$name-fastopt.js")
      .find(name => getClass.getResource(s"/public/$name") != null)
      .map(controllers.routes.Assets.versioned(_).url)
  }
}

class Application @Inject()(
    implicit val config: Configuration,
    env: Environment,
    implicit val actorSystem: ActorSystem
) extends InjectedController
    with AkkaContext {

  val apiService = new ApiService()

  def autowireApi(path: String): Action[RawBuffer] = Action.async(parse.raw) { implicit request =>
    // get the request body as ByteString
    val b = request.body.asBytes(parse.UNLIMITED).get

    // call Autowire route
    Router
      .route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(b.asByteBuffer))
      )
      .map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        Ok(data)
      })
  }

  def index = Action {
    Ok(views.html.index("Foulkon UI"))
  }

  def logging = Action(parse.anyContent) { request =>
    request.body.asJson.foreach { msg =>
      println(s"CLIENT - $msg")
    }
    Ok("")
  }
}
