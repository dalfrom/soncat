package soncat.server

trait Server {
  def startup(): Unit
  def shutdown(): Unit
  def awaitShutdown(): Unit
  def forceShutdown(): Unit
}

object Server {
  //def apply(): Server = new SoncatServer
}