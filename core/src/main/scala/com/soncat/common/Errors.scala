package soncat.common

object errors {
    // Generic errors
    var ERR_INVALID_ARGUMENT = "Invalid argument."
    var ERR_INVALID_STATE = "Invalid state."
    var ERR_INVALID_OPERATION = "Invalid operation."
    var ERR_INVALID_CONFIGURATION = "Invalid configuration."
    var ERR_INVALID_RESPONSE = "Invalid response."
    var ERR_INVALID_REQUEST = "Invalid request."

    // Server-related errors
    var ERR_SRV_ALREADY_RUNNING = "[sys] Server is already running."
    var ERR_SRV_ALREADY_RUNNING_CANT_START = "[sys] Server is already running. Can't start again."
    var ERR_SRV_ALREADY_SHUTTING_DOWN = "[sys] Server is already shutting down."
    var ERR_SRV_ALREADY_SHUTDOWN = "[sys] Server is already shutdown."
    var ERR_SRV_STARTING_UP = "[sys] Error starting up the soncat server. Shutting down..."
    var ERR_SRV_SHUTTING_DOWN_STARTING_UP = "[sys] The soncat server cannot be shut down while starting up. Wait for the server to start up before shutting down."
    var ERR_SRV_SHUTTING_DOWN = "[sys] Error shutting down the soncat server. Awaiting..."

    // Configuration-related errors
    var ERR_CFG_INVALID_FILE = "[cfg] Invalid configuration file."
    var ERR_CFG_INVALID_FORMAT = "[cfg] Invalid configuration format."
    var ERR_CFG_INVALID_KEY = "[cfg] Invalid configuration key."
    var ERR_CFG_INVALID_VALUE = "[cfg] Invalid configuration value."
    var ERR_CFG_INVALID_TYPE = "[cfg] Invalid configuration type."
    var ERR_CFG_INVALID_SECTION = "[cfg] Invalid configuration section."
}
