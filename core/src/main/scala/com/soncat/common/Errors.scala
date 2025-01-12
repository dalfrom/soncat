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
    var ERR_CFG_FAILED_TO_LOAD = "[cfg] Failed to load configuration."
    var ERR_CFG_INVALID_FILE = "[cfg] Invalid configuration file."
    var ERR_CFG_INVALID_FORMAT = "[cfg] Invalid configuration format."
    var ERR_CFG_INVALID_KEY = "[cfg] Invalid configuration key."
    var ERR_CFG_INVALID_VALUE = "[cfg] Invalid configuration value."
    var ERR_CFG_INVALID_TYPE = "[cfg] Invalid configuration type."
    var ERR_CFG_INVALID_SECTION = "[cfg] Invalid configuration section."

    // Parsing data-related errors
    var ERR_PARSE_INVALID_DATA = "[parse] Unable to parse due to possible invalid data: "

    // Wal-related errors
    var ERR_WAL_UNABLE_TO_OPEN = "[wal] Unable to open WAL file or its stream: "
    var ERR_WAL_UNABLE_TO_CLOSE = "[wal] Unable to close WAL file or its stream: "
    var ERR_WAL_INVALID_FILE = "[wal] Invalid WAL file."
    var ERR_WAL_INVALID_FORMAT = "[wal] Invalid WAL format."
    var ERR_WAL_INVALID_KEY = "[wal] Invalid WAL key."
    var ERR_WAL_UNWRITABLE = "[wal] Unable to write to WAL file: "

    // Memtable-related errors
    var ERR_MEMTABLE_INVALID_SIZE = "[memtable] Invalid size."
    var ERR_MEMTABLE_INVALID_CONFIG = "[memtable] Invalid configuration."
    var ERR_MEMTABLE_INVALID_THRESHOLD = "[memtable] Invalid threshold."
    var ERR_MEMTABLE_INVALID_MAX_KEY_SIZE = "[memtable] Invalid max key size."
    var ERR_MEMTABLE_INVALID_KEY_COUNT = "[memtable] Invalid key count."
    var ERR_MEMTABLE_COULD_NOT_FLUSH = "[memtable] Couldn't flush the MemTable: "
    var ERR_MEMTABLE_COULD_NOT_SAVE = "[memtable] Couldn't save the data to the MemTable: "
}
