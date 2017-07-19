package client.routes

class AppRouter {

  sealed trait Page

  case object UserPage extends Page

}
