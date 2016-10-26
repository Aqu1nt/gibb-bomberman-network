package application.network.protocol.server;

import application.network.protocol.Message;

import java.util.function.BiConsumer;

/**
 * Diese Schnittstelle muss von einer Klasse innerhalb der Bomberman-Server-Komponente
 * implementiert werden. Sie erlaubt dem Server-Objekt der Netzwerkschicht, von den 
 * Clients empfangene Nachrichten zur Verarbeitung an die Applikationsschicht weiterzuleiten.
 * 
 * @author Andres Scheidegger
 *
 */
public interface ServerApplicationInterface extends BiConsumer<Message, String>
{
  /**
   * Delegates to {@link #handleMessage(Message, String)}
   * @param message die Nachricht
   * @param id die Client Id
   */
  @Override
  default void accept(Message message, String id)
  {
    this.handleMessage(message, id);
  }

  /**
   * Diese Methode wird vom Server-Objekt aufgerufen, wenn eine Nachricht von einem Client
   * empfangen wurde.
   * @param message Das empfangene Nachrichtenobjekt.
   * @param connectionId Die Netzwerkverbindung, über welche die Nachricht empfangen wurde
   * (repräsentiert den Client).
   */
   void handleMessage(Message message, String connectionId);
}
