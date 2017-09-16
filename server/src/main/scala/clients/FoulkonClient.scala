package clients

import contexts.AkkaContext

trait FoulkonClient extends FoulkonConfig with FoulkonUserClient {self: AkkaContext => }
