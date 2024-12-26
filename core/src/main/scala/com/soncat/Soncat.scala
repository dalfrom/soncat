package soncat

import soncat.server.{SoncatServer, Server}
import soncat.common.OptionParser.{parseOptions}
import java.util.Properties


object Soncat {

    /**
      * Parse the command line arguments and return a Properties object
      * with the parsed arguments.
      *
      * This is a simple implementation and should be improved in order to be more dynamic
      * and accept more fields without the need to manually add those.
      *
      * @param args
      * @return
      */
    def configFromArgs(args: Array[String]): Properties = {
        val props = new Properties()

        // Parse the command line arguments
        val options = parseOptions(args)

        options.foreach {
            case (key, value) => {
                key match {
                    case "port" => props.setProperty("port", value)
                    case "config-path" => {
                        if (value.endsWith(".json")) {
                            props.setProperty("config_path", value)
                        }
                        else {
                            println(s"Invalid configuration file: ${value}")
                        }
                    }
                    case "verbose" => props.setProperty("verbose", true.toString())
                    case "debug" => props.setProperty("debug", true.toString())
                    case _ => println(s"Invalid argument: ${key}")
                }
            }
        }

        props
    }

    def newServer(properties: Properties): Server = {
        new SoncatServer(
            properties
        )
    }

    def main(args: Array[String]): Unit = {
        val properties = configFromArgs(args)
        val server = newServer(
            properties
        )

        try server.startup()
        catch {
            case e: Exception => {
                println(s"Failed to start Soncat server: ${e.getMessage()}")
                System.exit(1)
            }
        }

        server.awaitShutdown()
    }
}