package soncat.server

import java.net.{ServerSocket, Socket}
import java.io.{InputStream, OutputStream}
import upickle.default._

object ConnectionHandler {

    // Contains all the connected clients on the server with their respective sockets
    // Map [clientAddressOrIdentifier => clientSocket]
    private var connectedClients = Map[String, Socket]()

    def startConnector(
        isRunning: Boolean = true
    ): Unit = {
        val port = 3108 // Define the port to listen on
        val serverSocket = new ServerSocket(port)
        println(s"Server started, listening on port $port")

        println(s"isRunning: $isRunning")

        while (isRunning) {
            // Accept a client connection
            val clientSocket = serverSocket.accept()
            println(s"Client connected: ${clientSocket.getInetAddress}")

            // Handle the client in a separate thread
            new Thread(
                new ClientHandler(
                    clientSocket,
                    addClient = addClient,
                    removeClient = removeClient
                )
            ).start()
        }
    }

    def addClient(clientAddressOrIdentifier: String, clientSocket: Socket): Unit = {
        connectedClients += (clientAddressOrIdentifier -> clientSocket)
    }

    def removeClient(clientAddressOrIdentifier: String): Unit = {
        connectedClients -= clientAddressOrIdentifier
    }

    def getConnectedClients(): Map[String, Socket] = {
        connectedClients
    }

    def endConnections(): Unit = {
        if (connectedClients.isEmpty) {
            println("No clients connected")
            return
        }

        // Close all client connections
        connectedClients.foreach {
            case (clientAddressOrIdentifier, clientSocket) =>
                clientSocket.close()
        }
    }
}

class ClientHandler(
    clientSocket: Socket,
    addClient: (String, Socket) => Unit = ConnectionHandler.addClient,
    removeClient: (String) => Unit = ConnectionHandler.removeClient
) extends Runnable {
    override def run(): Unit = {
        val inputStream: InputStream = clientSocket.getInputStream
        val outputStream: OutputStream = clientSocket.getOutputStream

        var identifier: String = null

        try {
            // Read data from the client
            val buffer = new Array[Byte](1024)
            var bytesRead = inputStream.read(buffer)

            while (bytesRead != -1) {
                val data = buffer.slice(0, bytesRead)
                val payload = data.map(_.toChar).mkString
                println(s"Received data: ${payload}") // For debugging, convert bytes to string

                // From the JSON payload I extract the "app_name" in the "data" field
                // and use it as the client identifier

                val clientIdentifierMap = read[Map[String, Map[String, String]]](payload) // """{ "data": { "app_name": "go_app" } }"""
                identifier = clientIdentifierMap("data")("app_name")

                addClient(identifier, clientSocket)

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
            removeClient(identifier)
        }
    }
}
