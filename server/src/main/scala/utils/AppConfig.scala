package utils

import com.typesafe.config.ConfigFactory

object AppConfig {
  private val config = ConfigFactory.load()

  object FoulkonConfig {
    private lazy val foulkonConfig = config.getConfig("app.foulkon")

    val foulkonUser: String = foulkonConfig.getString("user.id")
    val foulkonPassword: String = foulkonConfig.getString("user.password")

    val foulkonHost: String = foulkonConfig.getString("adress.host")
    val foulkonPort: Int = foulkonConfig.getInt("adress.port")
  }
}
