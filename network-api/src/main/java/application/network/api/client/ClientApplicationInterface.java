package application.network.api.client;

import application.network.api.Message;

import java.util.function.Consumer;

/**
 * Diese Schnittstelle muss von einer Klasse innerhalb der Bomberman-Client-Komponente
 * implementiert werden. Sie erlaubt dem ServerProxy-Objekt der Netzwerkschicht, vom 
 * Server empfangene Nachrichten zur Verarbeitung an die Applikationsschicht weiterzuleiten.
 * 
 * @author Andres Scheidegger
 *
 */
public interface ClientApplicationInterface extends Consumer<Message>
{
  /**
   * Delegate to {@link #handleMessage(Message)}
   */
  @Override
  default void accept(Message message)
  {
    this.handleMessage(message);
  }

  /**
   * Diese Methode wird vom ServerProxy-Objekt aufgerufen, wenn eine Nachricht vom Server
   * empfangen wurde.
   * @param message Das empfangene Nachrichtenobjekt.
   */
  void handleMessage(Message message);
}
