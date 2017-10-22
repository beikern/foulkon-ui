package client.components.utils

import java.util.regex.Pattern

object FoulkonRegexPatterns {
  val externalIdPattern: Pattern    = "^[\\w+.@=\\-_]+$".r.pattern
  val orgPattern: Pattern           = "^[\\w\\-_]+$".r.pattern
  val pathPattern: Pattern          = "^/$|^/[\\w+/\\-_]+\\w+/$".r.pattern
  val namePattern: Pattern          = "^[\\w\\-_]+$".r.pattern
  val actionPattern: Pattern        = "^[\\w\\-_:]+[\\w\\-_*]+$".r.pattern
  val actionExcludePattern: Pattern = "[*]{2,}|[:]{2,}".r.pattern
}
