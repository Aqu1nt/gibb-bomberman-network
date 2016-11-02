package application.network.impl.scala.events

/**
  * Created by Emil on 30.10.2016.
  */
class ClientId(private var _clientId: String) extends Serializable {

  def clientId(): String = {
    _clientId
  }

  def clientId(id: String): Unit = {
    _clientId = id
  }
}