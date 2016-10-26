package application.network.protocol.server;

import application.network.protocol.Message;

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
   * @throws IOException when der Port nicht geöffnet werden konnte
   */
  void listen(int port) throws IOException;

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
   */
  void send(Message message, String clientId) throws IOException;

  /**
   * Verschickt eine Nachricht an alle verbundenen Clients
   * @param message die Nachricht welche gebroadcastet wird
   */
  void broadcast(Message message);

  /**
   * Registriert einen Handler welcher all eingehenden Nachrichten zusammen
   * mit ihrer ClientId erhält und diese weiterverarbeiten kann
   * @param handler der Handler welche alle eingehenden Nachrichten erhält
   */
  void addMessageHandler(BiConsumer<Message, String> handler);

  /**
   * Registriert einen Handler welcher einmalig pro neuer Client Verbindung aufgerufen wird
   * @param clientConnectedHandler der Handler
   */
  void addClientConnectedHandler(Consumer<String> clientConnectedHandler);

  /**
   * Registriert einen Handler welcher aufgerufen wird sobald ein Client
   * auf eine beliebige Weise die Verbindung zum Server schliesst
   * @param disconnectHandler wird aufgerufen mit der Id des Clients dessen Verbindung geschlossen wurde
   */
  void addClientDisconnectedHandler(Consumer<String> disconnectHandler);
}
