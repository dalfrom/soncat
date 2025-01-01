package soncat.io.config

import scala.io.Source
import upickle.default._
import soncat.common.errors._
import java.util.Properties


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
    memtable: MemtableConfig,
)

case class MemtableConfig(
    trie_max_key_size: Int,
    trie_max_depth: Int,
    size_threshold: Int,
)

object ConfigHandler {

    private val isProduction = sys.env.getOrElse("ENV", "development") == "production"
    private val defaultFileName = "configuration.soncat.json"
    // private var config: Config = null

    implicit val walConfigRw: ReadWriter[WalConfig] = macroRW
    implicit val dbConfigRw: ReadWriter[DbConfig] = macroRW
    implicit val memtableConfigRw: ReadWriter[MemtableConfig] = macroRW
    implicit val coreConfigRw: ReadWriter[CoreConfig] = macroRW
    implicit val dataRW: ReadWriter[Config] = macroRW

    var config: Config = null

    def loadConfiguration(properties: Properties): Config = {
        var configurationPath: String = null

        if (properties != null) {
            configurationPath = properties.getOrDefault("config_path", defaultFileName).toString()
            properties.getOrDefault("config_path", defaultFileName).toString()
            if (!configurationPath.endsWith(".json")) {
                throw new RuntimeException(ERR_CFG_INVALID_FILE)
            }

            if (configurationPath == null || configurationPath.isEmpty()) {
                configurationPath = defaultFileName
            }
        }

        val defaultConfigPath = if (isProduction) "/etc/soncat/configuration.soncat.json" else configurationPath
        try {
            val configFile = Source.fromFile(defaultConfigPath).getLines().mkString
            if (! isAlreadyLoaded()) {
                config = read[Config](configFile)
            }
            return config
        } catch {
            case e: Exception => {
                throw new RuntimeException(s"${ERR_CFG_FAILED_TO_LOAD}${e.getMessage()}")
            }
        }
    }

    private def isAlreadyLoaded(): Boolean = {
        config != null
    }

    def getConfig(): Config = {
        if (! isAlreadyLoaded()) {
            config = loadConfiguration(null)
        }

        return config
    }
}
