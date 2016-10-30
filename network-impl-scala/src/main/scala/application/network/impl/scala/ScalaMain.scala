package application.network.impl.scala

import application.network.api.Network

/**
  * Created by Emil on 30.10.2016.
  */
object ScalaMain {
  def main(args: Array[String]): Unit = {
    val server = Network.createServer()
    val client = Network.createClient()

    server.listen(20000)
    Thread.sleep(1000)
    client.connect("client", "localhost", 20000)
    Thread.sleep(1000)
    server.shutdown()
    client.disconnect()
  }
}
