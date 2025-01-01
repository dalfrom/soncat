package soncat.db.memtable

import soncat.io.config.{ConfigHandler, Config}
import soncat.server.{StorageLogData, IncomingLogData}
import soncat.common.errors._


trait TMemtable {
    def saveData(data: StorageLogData): Unit
    def getData(): String
}

class Memtable(
    val config: Config = ConfigHandler.getConfig(),
    val max_treshold: Int = ConfigHandler.getConfig().core.db.memtable.size_threshold * 1024 * 1024, // Convert MB to Bytes
    val trieMemtable: TrieMemtable[String, String] = new TrieMemtable[String, String](ConfigHandler.getConfig().core.db.memtable)
) extends TMemtable {
    // This is the entry point for the MemTable workflow. Here's a breakdown:
    // 1. Data comes in from the DataInputHandler
    // 2. Then, using TrieMemtable, we save the data into the MemTable which is a B-Tree structure in-memory
    //    Here's the good thing, since we never update the data or delete specific elements of it,
    //    the B-Tree won't be unbalanced and we can always find the data in O(log n) time.
    // 3. Then, based on configuration.soncat.json, we will save the data into the disk as well depending
    //    on the size of the MemTable and the retention policy.
    // 4. The MemTable will also be responsible for handling the data that is sent back to the client
    // 5. Memtable will use Flushing to write the data to disk and will also use Compaction to merge the data
    //    from the MemTable to the SSTable. The same file will invoke WalDirector to write the delete the WAL
    //    up to this point and create a blank file for the next incoming data.
    // 6. In case of a crash, the MemTable will use the WAL to recover the data and then write it to the MemTable
    //    and SSTable. Recovering.scala will be responsible for this.


    override def saveData(data: StorageLogData): Unit = {
        try {
            // The incoming data will be saved in the MemTable

            if (trieMemtable.size() >= max_treshold) {
                val flusher = new Flushing[String, String]()

                flusher.flushAndErase(trieMemtable)
            }

            trieMemtable.put(data._id, data.toString())
        } catch {
            case e: Exception => println(s"${ERR_MEMTABLE_COULD_NOT_SAVE}${e.getMessage}")
        }
    }

    override def getData(): String = {
        // Get the data from the MemTable
        trieMemtable.getAllData().mkString("\n")
    }
}
