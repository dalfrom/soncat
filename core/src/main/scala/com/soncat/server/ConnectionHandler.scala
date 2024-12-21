package soncat.server

import java.net.{ServerSocket, Socket}

object ConnectionHandler {
    def startConnector(
        isRunning: Boolean = true
    ): Unit = {
        val port = 3108 // Define the port to listen on
        val serverSocket = new ServerSocket(port)
        println(s"Server started, listening on port $port")

        while (isRunning) {
            // Accept a client connection
            val clientSocket = serverSocket.accept()
            println(s"Client connected: ${clientSocket.getInetAddress}")

            // Handle the client in a separate thread
            new Thread(
                new ClientHandler(
                    clientSocket
                )
            ).start()
        }
    }
}
