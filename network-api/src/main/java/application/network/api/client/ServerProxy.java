package application.network.api.client;

import application.network.api.Message;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Diese Klasse repräsentiert den Server auf der Client-Seite und zwar auf Ebene Netzwerkschicht.
 * Sie definiert die Schnittstelle, welche die Netzwerkschicht der Bomberman-Client-Komponente anbietet.
 * Sie ist abstrakt und muss innerhalb der Netzwerkschicht durch Ableitung implementiert werden.
 * Die Bomberman-Client-Komponente muss beim Start ein Objekt von dieser Implementationsklasse
 * erzeugen.
 * 
 * @author Andres Scheidegger
 *
 */
public interface ServerProxy
{

  /**
   * Verbindet den Client auf den Ziel Server
   * @param clientId die Id dieses Clients
   * @param ip die Ziel IP
   * @param port der Ziel Port
   * @throws IOException wenn die Verbindung fehlschlägt
   * @throws ClientIdInUseException wenn die gegebene client id bereits exisitert auf dem server
   * @throws LobbyFullException wenn die Lobby keine weiteren Spieler zulässt
   * @throws NullPointerException wenn die clientId oder die ip null ist
   */
  void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException;

  /**
   * Schliesst die Verbindung zum Server
   */
  void disconnect();

  /**
   * Sendet ein Nachrichtenobjekt an den Server. Diese Methode muss innerhalb der Netzwerkschicht
   * implementiert werden.
   * @param message Das Nachrichtenobjekt, welches an den Server gesendet werden soll.
   * @throws NullPointerException wenn die Message null ist
   */
  void send(Message message);

  /**
   * Registriert einen Handler welcher mit jeder eingehenden Nachricht
   * aufgerufen wird
   * @param handler der Handler
   * @throws NullPointerException wenn der handler null ist
   */
  void addMessageHandler(Consumer<Message> handler);

  /**
   * Registriert einen speziellen Handler welcher nur die Nachrichten des
   * gegebenen Typs oder eines Subtyps erhält
   * @param msgType der Nachrichtentyp
   * @param handler der Handler
   * @param <T> der generische Typ aller Nachrichten
   */
  @SuppressWarnings("unchecked")
  default <T extends Message> void addMessageHandler(Class<T> msgType, Consumer<T> handler)
  {
    addMessageHandler(msg -> {
      if (msgType.isInstance(msg)) {
        handler.accept((T) msg);
      }
    });
  }

  /**
   * Registriert einen Handler welcher in jedem Fall aufgerufen wird wenn
   * die Socketverbindung zum Server geschlossen wird
   * @param handler der Handler
   * @throws NullPointerException wenn der handler null ist
   */
  void addServerDisconnectedHandler(Runnable handler);
}
