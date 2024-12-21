package soncat.server

import java.net.{ServerSocket, Socket}
import java.io.{InputStream, OutputStream}
import upickle.default._
import java.text.DateFormat


class ClientHandler(
    clientSocket: Socket,
) extends Runnable {
    case class IncomingLogData(
		// TODO: Understand how we can remove String from here since I want to force only those 5 types
		`type`: "info" | "error" | "warning" | "success" | "notice" | String,
		sent_at: String, // General timestamp
		service: String,
		payload: String,
		p_type:  String, // "json" | "csv" | "xml" | anything that can be helpful here...
	)
    case class LogResponseData(data: IncomingLogData)

    override def run(): Unit = {
        val inputStream: InputStream = clientSocket.getInputStream
        val outputStream: OutputStream = clientSocket.getOutputStream

        implicit val dataRW: ReadWriter[IncomingLogData] = macroRW
        implicit val logResponseDataRw: ReadWriter[LogResponseData] = macroRW

        try {
            // Read data from the client
            val buffer = new Array[Byte](1024)
            var bytesRead = inputStream.read(buffer)

            while (bytesRead != -1) {
                val data = buffer.slice(0, bytesRead)
                val payload = data.map(_.toChar).mkString
                // val clientIdentifierMap = read[LogResponseData](payload)

                // Push the data to the handler, in order to have a centralized place to handle all incoming data
                DataInputHandler.handleIncomingData(payload)

                // Echo data back to the client (optional)
                outputStream.write(data)
                outputStream.flush()

                // Read the next chunk
                bytesRead = inputStream.read(buffer)
            }
        } catch {
            case ex: Exception => println(s"Error handling client: ${ex.getMessage}")
        } finally {
            println("Client disconnected")
            clientSocket.close()
        }
    }
}
