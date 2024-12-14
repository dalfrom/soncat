package soncat

import soncat.server.SoncatServer


object Soncat {
    def main(args: Array[String]): Unit = {
        println("Hello, Soncat!")

        SoncatServer.startup()
    }
}