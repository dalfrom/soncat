package soncat.server

import java.util.concurrent.atomic.{AtomicBoolean}
import soncat.common.errors._
import soncat.server.ConnectionHandler
import soncat.io.config.ConfigHandler
import java.util.Properties


class SoncatServer(
    properties: Properties
) extends Server {

    private val isRunning = new AtomicBoolean(false)
    private val isShuttingDown = new AtomicBoolean(false)
    private val isStartingUp = new AtomicBoolean(false)

    override def startup(): Unit = {
        try {
            println("Soncat server is starting up...")

            if (isRunning.get()) {
                throw new IllegalStateException(ERR_SRV_ALREADY_RUNNING_CANT_START)
            }

            val canStartup = isStartingUp.compareAndSet(false, true)
            if (! canStartup) {
                throw new IllegalStateException(ERR_SRV_ALREADY_RUNNING)
            }

            isStartingUp.set(true)

            // Loading configuration from the configuration file
            val configuration = ConfigHandler.loadConfiguration(
                properties
            )

            // Start the server:
            // To start, the database will be started, as well as with the cache and WAL mechanism
            // (Unless configured to do so, anything saved on disk will NOT be loaded into memory. Check configuration on yaml)
            // The server will initialize and start accepting connections

            // Setting the system to be running
            isRunning.set(true)

            var port: Int = if (properties.getProperty("port") != null) {
                properties.getProperty("port").toInt
            } else {
                configuration.core.port
            }


            // Starting the server, as everything before it was successfully initialized
            ConnectionHandler.startConnector(
                isRunning.get(),
                port
            )
        }
        catch {
            case e: Exception => {
                println(s"${ERR_SRV_STARTING_UP}${e.getMessage()}")
                isStartingUp.set(false)
                shutdown()

                throw e
            }
        }
    }

    override def shutdown(): Unit = {
        try {
            println("Soncat server is shutting down...")

            if (isStartingUp.get()) {
                throw new IllegalStateException(ERR_SRV_SHUTTING_DOWN_STARTING_UP)
            }

            if (isShuttingDown.get()) {
                throw new IllegalStateException(ERR_SRV_ALREADY_SHUTTING_DOWN)
            }

            val canShutdown = isRunning.compareAndSet(true, false)

            // To start, we send a signal to the server to stop accepting new connections
            // The server will wait for the latest connections to finish before closing them with a specific message | code
            // Then, the server will close and deny any connections
            // It will save on disk I/O any data that needs to be saved (from LSM trees to SSTables)
            // It will close the connections to the database
            // It will close the connections to the cache
            // It will lastly turn off the server along with the database and nullify the cache
            // Once all of this is done, the server will be in a state of shutdown, ready to be started up again

            if (! canShutdown) {
                throw new IllegalStateException(ERR_SRV_ALREADY_SHUTDOWN)
            }

            isShuttingDown.set(true)
        }
        catch {
            case e: Exception => {
                println(ERR_SRV_SHUTTING_DOWN)
                throw e
            }
        }
    }

    override def awaitShutdown(): Unit = {
        // Await
    }

    override def forceShutdown(): Unit = {
        println("Soncat server is force shutting down...")

        // This will check if the server is running and if it is, it will shut it down
        // It will also check if the server is gracefully shutting down and if it is
        // it will force and shut down everything without awaiting for systems to end

        // WARNING:
        // This will cause data loss and corruption if not used properly
        // Any incoming data will be lost, any data that is being processed will be lost
        // Nothing that is saved in the database or cache will be saved
        // Non-written to disk data will be lost

    }

    private def stopStartup(): Unit = {
        isStartingUp.set(false)

        // This will check if the server is running and if it is, it will shut it down
        // It will also check if the server is gracefully shutting down and if it is
        // it will await and shut down everything with caution and awaiti for procedures to finish

        if (isStartingUp.get()) {
            shutdown()
        }
    }
}
