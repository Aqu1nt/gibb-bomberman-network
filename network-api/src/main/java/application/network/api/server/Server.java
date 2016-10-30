package application.network.api.server;

import application.network.api.Message;
import application.network.api.client.LobbyFullException;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Diese Klasse definiert die Schnittstelle, welche die Netzwerkschicht der Bomberman-Server-Komponente
 * anbietet. Die Klasse ist abstrakt und muss innerhalb der Netzwerkschicht durch Ableitung implementiert
 * werden. Die Bomberman-Server-Komponente muss beim Start ein Objekt von dieser Implementationsklasse
 * erzeugen.
 * 
 * @author Andres Scheidegger
 *
 */
public interface Server
{
  /**
   * Startet den Server auf dem angegebenen Port, sobald diese
   * Methode abgeschlossen ist, wird der Server alle neuen Verbindungen
   * welche auf diesem Port eingehen annehmen.
   * @param port der Port auf welchem der Server gestartet wird
   * @throws IOException wenn der Port nicht geöffnet werden konnte
   */
  void listen(int port) throws IOException, IllegalStateException;

  /**
   * Stoppt den Server, dieser Methodenaufruf hat zur Folge
   * das alle Sockets geschlossen werden und der Port freigegeben
   * wird
   */
  void shutdown();

  /**
   * Schickt die gegebene Nachricht an den angegebenen Client
   * @param message die Nachricht welche verschickt werden soll
   * @param clientId die Id des Ziel clients
   * @throws NullPointerException wenn die Message oder die clientId null ist
   */
  void send(Message message, String clientId);

  /**
   * Verschickt eine Nachricht an alle verbundenen Clients
   * @param message die Nachricht welche gebroadcastet wird
   * @throws NullPointerException wenn die Message null ist
   */
  void broadcast(Message message);

  /**
   * Registriert einen Handler welcher all eingehenden Nachrichten zusammen
   * mit ihrer ClientId erhält und diese weiterverarbeiten kann
   * @param handler der Handler welche alle eingehenden Nachrichten erhält
   * @throws NullPointerException wenn der handler null ist
   */
  void addMessageHandler(BiConsumer<Message, String> handler);

  /**
   * Registriert eine speziell Form von Message Handler. Dieser Handler wird alle Nachrichten des
   * gegebenen Typs an den {@link Consumer} welcher die handlerMapper {@link Function} zurückgibt
   * weiterleiten, dadurch wird das Dispatchen der Nachricht zum richtigen Empfänger direkt mithilfe des
   * Mappers gemacht
   * @param handlerMapper der Mapper welche der ClientId den richtigen Handler zuteilt
   */
  @SuppressWarnings("unchecked")
  default void addDispatchingMessageHandler(Function<String, Consumer<Message>> handlerMapper)
  {
    addMessageHandler((msg, id) -> handlerMapper.apply(id).accept(msg));
  }

  /**
   * Registriert einen Handler welche nur Nachrichten eines bestimment Typs (msgType & Subtypen)
   * an den gegebenen Handler weiterleitet
   * @param msgType der geforderte Nachrichtentyp
   * @param handler der Handler welcher alle Nachrichten vom typ "msgType" erhält
   * @param <T> der generische Typ der {@link Message}
   */
  @SuppressWarnings("unchecked")
  default <T extends Message> void addMessageHandler(Class<T> msgType, BiConsumer<T, String> handler)
  {
    addMessageHandler((msg, id) -> {
      if (msgType.isInstance(msg)) {
        handler.accept((T) msg, id);
      }
    });
  }

  /**
   * Registriert eine speziell Form von Message Handler. Dieser Handler wird alle Nachrichten des
   * gegebenen Typs an den {@link Consumer} welcher die handlerMapper {@link Function} zurückgibt
   * weiterleiten, dadurch wird das Dispatchen der Nachricht zum richtigen Empfänger direkt mithilfe des
   * Mappers gemacht
   * @param msgType der geforderte Nachrichtentyp
   * @param handlerMapper der Mapper welche der ClientId den richtigen Handler zuteilt
   * @param <T> der generische Typ der {@link Message}
   */
  @SuppressWarnings("unchecked")
  default <T extends Message> void addDispatchingMessageHandler(Class<T> msgType, Function<String, Consumer<T>> handlerMapper)
  {
    addMessageHandler(msgType, (msg, id) -> handlerMapper.apply(id).accept(msg));
  }

  /**
   * Registriert einen Handler welcher einmalig pro neuer Client Verbindung aufgerufen wird
   * Der Handler gibt zurück ob die Verbindung angenommen werden soll oder nicht, im Falle
   * einer Ablehnung (false) muss auf dem Client eine {@link LobbyFullException} geworfen werden!
   *
   * Für den Fall, dass mehrere Handler registriert sind und unterschiedliche Werte zurückgeben
   * reicht EINE Ablehnung (ein oder mehrere Hndler geben false zurück) um die Verbindung abzulehnen.
   *
   * Wenn keine Handler registriert sind wird die Verbindung in jedem Fall angenommen.
   *
   * @param handler der Handler
   * @throws NullPointerException wenn der client null ist
   */
  void addClientConnectedHandler(Function<String, Boolean> handler);

  /**
   * Registriert einen Handler welcher aufgerufen wird sobald ein Client
   * auf eine beliebige Weise die Verbindung zum Server schliesst
   * @param handler wird aufgerufen mit der Id des Clients dessen Verbindung geschlossen wurde
   * @throws NullPointerException wenn der handler null ist
   */
  void addClientDisconnectedHandler(Consumer<String> handler);
}
