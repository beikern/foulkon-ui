package controllers

import java.nio.ByteBuffer

import boopickle.Default.{Pickle, Pickler, Unpickle}
import com.google.inject.Inject
import play.api.{Configuration, Environment}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

object Router extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)
  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}

class Application @Inject() (implicit val config: Configuration, env: Environment) extends Controller {

  def index = Action {
    Ok(views.html.index("SPA tutorial"))
  }

  def logging = Action(parse.anyContent) {
    implicit request =>
      request.body.asJson.foreach { msg =>
        println(s"CLIENT - $msg")
      }
      Ok("")
  }
}