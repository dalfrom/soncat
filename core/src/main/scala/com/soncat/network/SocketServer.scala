package soncat.network

import java.net.{ServerSocket, Socket}
import java.io.{InputStream, OutputStream}
import upickle.default._
import java.text.DateFormat
import soncat.server.DataInputHandler


class SocketServer {
    def startServer(
        isRunning: Boolean = true,
        corePort: Int = 3108
    ): Unit = {
        val port = corePort // Define the port to listen on
        val serverSocket = new ServerSocket(port)
        println(s"Server started, listening on port $port")

        while (isRunning) {
            // Accept a client connection
            val clientSocket = serverSocket.accept()
            println(s"Client connected: ${clientSocket.getInetAddress}")

            // Handle the client in a separate thread
            new Thread(
                new SocketClient(
                    clientSocket
                )
            ).start()
        }
    }
}

class SocketClient(
    clientSocket: Socket
) extends Runnable {
    case class IncomingLogData(
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
