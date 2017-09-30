package clients

import contexts.AkkaContext

trait FoulkonClient extends FoulkonConfig with FoulkonUserClient with FoulkonGroupClient { self: AkkaContext =>
}
