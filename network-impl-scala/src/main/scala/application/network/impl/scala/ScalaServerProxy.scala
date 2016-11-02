package application.network.impl.scala

import java.io.ObjectOutputStream
import java.net.Socket
import java.util.function.Consumer

import application.network.api.Message
import application.network.api.client.ServerProxy
import application.network.impl.scala.events.ClientId

/**
  * Created by Emil on 29.10.2016.
  */
class ScalaServerProxy extends ServerProxy
{
  var socket: Socket = _
  var out: ObjectOutputStream = _

  override def connect(clientId: String, ip: String, port: Int): Unit = {
    socket = new Socket(ip, port)
    out = new ObjectOutputStream(socket.getOutputStream)
    out.writeObject(new ClientId(clientId))
  }

  override def disconnect(): Unit = {
    if (socket != null) {
      socket.close()
    }
  }

  override def send(message: Message): Unit = ???

  override def addMessageHandler(handler: Consumer[Message]): Unit = ???

  override def addServerDisconnectedHandler(handler: Runnable): Unit = ???
}
