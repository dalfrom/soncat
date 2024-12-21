package soncat.io.wal

import java.io.{File, FileOutputStream, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import soncat.common.errors._


object WalDirector {
    val BASE_DIR = "wal" // "/var/lib/soncat/wal"

    def writeToWal(payload: String): Unit = {
        try {
            val path = os.pwd / s"${BASE_DIR}"
            if (! os.exists(path)) {
                os.makeDir(path)
            }

            val walFile = new File(s"$BASE_DIR/wal.log")
            if (! walFile.exists()) {
                walFile.createNewFile()
            }

            val outputStream: FileOutputStream = new FileOutputStream(walFile, true) // Append mode

            val logLine = s"${System.currentTimeMillis()} |> ${payload}\n"

            val logBytes = logLine.getBytes("UTF-8")
            println(logBytes)
            val buffer = ByteBuffer.allocate(
                logBytes.length
            )
            buffer.put(logBytes)

            // Write buffer to the WAL file
            outputStream.write(buffer.array())
            outputStream.flush() // Ensure data is written to disk
        } catch {
            case e: Exception => println(s"${ERR_WAL_UNWRITABLE}${e.getMessage}")
        }
    }
}

