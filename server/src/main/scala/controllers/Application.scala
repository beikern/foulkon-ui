package controllers

import java.nio.ByteBuffer

import boopickle.Default.{Pickle, Pickler, Unpickle}
import com.google.inject.Inject
import play.api.{Configuration, Environment}
import play.api.mvc.{Action, Controller}
import services.ApiService
import boopickle.Default._
import shared.Api

import scala.concurrent.ExecutionContext.Implicits.global

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

class Application @Inject()(implicit val config: Configuration, env: Environment) extends Controller {
  val apiService = new ApiService()

  def autowireApi(path: String) = Action.async(parse.raw) {
    implicit request =>
      println(s"Request path: $path")

      // get the request body as ByteString
      val b = request.body.asBytes(parse.UNLIMITED).get

      // call Autowire route
      Router.route[Api](apiService)(
        autowire.Core.Request(path.split("/"), Unpickle[Map[String, ByteBuffer]].fromBytes(b.asByteBuffer))
      ).map(buffer => {
        val data = Array.ofDim[Byte](buffer.remaining())
        buffer.get(data)
        Ok(data)
      })
  }

  def index = Action {
    Ok(views.html.index("SPA tutorial"))
  }

  def logging = Action(parse.anyContent) { implicit request =>
    request.body.asJson.foreach { msg =>
      println(s"CLIENT - $msg")
    }
    Ok("")
  }
}
