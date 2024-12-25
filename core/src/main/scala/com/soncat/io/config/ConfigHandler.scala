package soncat.io.config

import scala.io.Source
import upickle.default._


case class Config(
    core: CoreConfig,
)

case class CoreConfig(
    port: Int,
    whitelisted_entities: Array[String],
    whitelisted_domains: Array[String],
    wal: WalConfig,
    db: DbConfig
)

case class WalConfig(
    enabled: Boolean,
    retention_time: Int,
    retention_size: Int,
    default_path: String
)

case class DbConfig(
    retention_time: Int,
    load_on_startup: Boolean,
)

object ConfigHandler {

    private val isProduction = sys.env.getOrElse("ENV", "development") == "production"
    private val defaultConfigPath = if (isProduction) "/etc/soncat/configuration.soncat.json" else "configuration.soncat.json"
    // private var config: Config = null

    implicit val walConfigRw: ReadWriter[WalConfig] = macroRW
    implicit val dbConfigRw: ReadWriter[DbConfig] = macroRW
    implicit val coreConfigRw: ReadWriter[CoreConfig] = macroRW
    implicit val dataRW: ReadWriter[Config] = macroRW

    var config: Config = null

    def loadConfiguration(): Config = {
        try {
            val configFile = Source.fromFile(defaultConfigPath).getLines().mkString
            if (! isAlreadyLoaded()) {
                config = read[Config](configFile)
            }
            return config
        } catch {
            case e: Exception => {
                throw new RuntimeException("Failed to load configuration")
            }
        }
    }

    private def isAlreadyLoaded(): Boolean = {
        config != null
    }

    def getConfig(): Config = {
        if (! isAlreadyLoaded()) {
            config = loadConfiguration()
        }

        return config
    }
}
