package application.network.impl.scala

import java.lang.Boolean
import java.net.ServerSocket
import java.util.concurrent.Executors
import java.util.function.{BiConsumer, Consumer, Function}

import application.network.api.Message
import application.network.api.server.Server

import scala.collection.mutable.ListBuffer

/**
  * Created by Emil on 29.10.2016.
  */
class ScalaServer() extends Server with Runnable
{
  private var socket: ServerSocket = _
  private val executors = Executors.newFixedThreadPool(20)
  private val connections = ListBuffer[ScalaServerConnection]()
  private val connectedHandlers = ListBuffer[Function[String, Boolean]]()
  private val messageHandlers = ListBuffer[BiConsumer[Message, String]]()
  private val disconnectedHandlers = ListBuffer[Consumer[String]]()

  override def listen(port: Int): Unit =
  {
    if (socket != null) {
      throw new IllegalStateException("Server is already running")
    }
    socket = new ServerSocket(port)
    executors.submit(this)
  }

  override def shutdown(): Unit =
  {
    if (socket != null)
    {
      socket.close()
      socket = null
    }
    executors.shutdown()
  }

  override def send(message: Message, clientId: String): Unit = ???

  override def broadcast(message: Message): Unit = ???

  override def addMessageHandler(handler: BiConsumer[Message, String]): Unit = messageHandlers += handler

  override def addClientConnectedHandler(handler: Function[String, Boolean]): Unit = connectedHandlers += handler

  override def addClientDisconnectedHandler(handler: Consumer[String]): Unit = disconnectedHandlers += handler

  override def run(): Unit =
  {
    val socket = this.socket //Maintain a local variable to prevent possible nullpointers
    while (!socket.isClosed)
    {
      val s = socket.accept()
      val connection = new ScalaServerConnection(this, s)
      connections += connection
      executors.submit(connection)
    }
  }
}
