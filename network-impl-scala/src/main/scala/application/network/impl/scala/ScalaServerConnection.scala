package application.network.impl.scala

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import application.network.api.Message

import scala.collection.mutable.ListBuffer

/**
  * Created by Emil on 29.10.2016.
  */
class ScalaServerConnection(val server: ScalaServer, val socket: Socket) extends Runnable with AutoCloseable {

  val out = new ObjectOutputStream(socket.getOutputStream)
  val in = new ObjectInputStream(socket.getInputStream)
  val closedHandlers = ListBuffer[() => _]()

  override def run(): Unit = {
    while (!socket.isClosed)
    {
      val o = in.readObject
      if (o.isInstanceOf[Message])
      {
        print(o)
      }
    }
  }

  override def close(): Unit = {
    socket.close()
    closedHandlers.foreach(c => c())
  }
}
