package soncat.server

import soncat.io.wal.WalDirector
import soncat.db.memtable.{Memtable, TrieMemtable}
import soncat.common.errors._
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import upickle.default._


case class StorageLogData(
	_id: String,
	// TODO: Understand how we can remove String from here since I want to force only those 5 types
	`type`: "info" | "error" | "warning" | "success" | "notice" | String,
	sent_at: String, // General timestamp
	received_at: String, // General timestamp
	service: String,
	payload: String,
	p_type: String,
)

case class IncomingLogData(
	// TODO: Understand how we can remove String from here since I want to force only those 5 types
	`type`: "info" | "error" | "warning" | "success" | "notice" | String,
	sent_at: String, // General timestamp
	service: String,
	payload: String,
	p_type:  String, // "json" | "csv" | "xml" | anything that can be helpful here...
)

object DataInputHandler {
	implicit val rw: ReadWriter[IncomingLogData] = macroRW
	// This file connects ConnectionHandler to WalDirector and all the other classes/services around Soncat
    // It is the (2nd) main entry point for the server to handle incoming data, as the primary entry point is ConnectionHandler
    // This will sort all the data and send it to the appropriate service for processing
    // It will also handle the data that is sent back to the client

    /**
      * Initially, data come in from the client and we receive it here
      * Then, we save it into the WAL as well as sending that into the MemTable
      * From there on, the DataInputHandler's work is done and it will be the
      * responsibility of the MemTable to handle the data. This class only returns the response
	*/
	def handleIncomingData(data: String): Unit = {
		// Write the data to the WAL
		WalDirector.writeToWal(data)

		var storageLogData: StorageLogData = null
		try {
			storageLogData = parseData(data)
		}
		catch {
			case e: Exception => println(s"${ERR_PARSE_INVALID_DATA}${e.getMessage}")
			return
		}

		// Write the data to the MemTable
		val memtable = new Memtable()

		memtable.saveData(storageLogData)
	}

	def parseData(incData: String): StorageLogData = {
		val data = read[IncomingLogData](incData)

		return new StorageLogData(
			_id = s"sc_${System.currentTimeMillis()}",
			`type` = data.`type`,
			sent_at = data.sent_at,
			received_at = new Timestamp(System.currentTimeMillis()).toString(),
			service = data.service,
			payload = data.payload,
			p_type = data.p_type,
		)
	}



    /**
      * The MemTable will itself handle the incoming parsed data and push it into memory;
      * if the table is full, it will push it into the SSTable and sort it, to then create a new MemTable
      * This is done in parallel, as the MemTable is a separate thread that is always running
	*/
}
