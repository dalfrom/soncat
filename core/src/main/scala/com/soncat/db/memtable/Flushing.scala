package soncat.db.memtable

import scala.reflect.ClassTag
import soncat.common.errors._


class Flushing[K: ClassTag : Ordering, V]() {
    def flushAndErase(trie: TrieMemtable[K, V]): Unit = {
        try {
            // Iterate over all the data in the MemTable
            trie.getAllData().foreach {
                case (k, v) =>
                    val value = v match {
                        // If the value is Some, then write the data to disk
                        case Some(value) => {
                            // Write the data to disk
                            flushToDisk(k, value)
                        }
                        // If the value is None, then throw an exception
                        case None => throw new Exception("Value is None")
                    }
            }

            // TODO:
            // I think this should be done after the data is successfully written to disk
            // If some data fails to write there should be a way to avoid deleting it unless it's written
            // However, if I can't write to disk after X attempts, I should probably delete it (perhaps the data is corrupted)
            // This should never happen, but it's good to have a fallback plan

            // Erase the data from the MemTable
            trie.wipe()
        } catch {
            case e: Exception => println(s"${ERR_MEMTABLE_COULD_NOT_FLUSH}${e.getMessage}")
        }
    }

    private def flushToDisk(k: K, v: V): Unit = {
        // Write the data to disk
        // TODO: Implement this
    }
}

