package application.network.api.server;

import application.network.api.Message;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
  void send(Message message, String clientId) throws IOException;

  /**
   * Verschickt eine Nachricht an alle verbundenen Clients
   * @param message die Nachricht welche gebroadcastet wird
   * @throws NullPointerException wenn die Message null ist
   */
  void broadcast(Message message) throws IOException;

  /**
   * Registriert einen Handler welcher all eingehenden Nachrichten zusammen
   * mit ihrer ClientId erhält und diese weiterverarbeiten kann
   * @param handler der Handler welche alle eingehenden Nachrichten erhält
   * @throws NullPointerException wenn der handler null ist
   */
  void addMessageHandler(BiConsumer<Message, String> handler);

  /**
   * Registriert einen Handler welcher einmalig pro neuer Client Verbindung aufgerufen wird
   * @param handler der Handler
   * @throws NullPointerException wenn der client null ist
   */
  void addClientConnectedHandler(Consumer<String> handler);

  /**
   * Registriert einen Handler welcher aufgerufen wird sobald ein Client
   * auf eine beliebige Weise die Verbindung zum Server schliesst
   * @param handler wird aufgerufen mit der Id des Clients dessen Verbindung geschlossen wurde
   * @throws NullPointerException wenn der handler null ist
   */
  void addClientDisconnectedHandler(Consumer<String> handler);
}
