package application.network.impl.a.client.event;

import application.network.api.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by nschaefli on 12/5/16.
 */
public interface EventManager extends EventDispatcher {

    /**
     * Fuegt ein neuer Nachrichtenempfangs Observer dem EventManager hinzu.
     * @param observer  Der Observer welcher die Nachricht konsumiert.
     */
    void addMessageReceivedObserver(Consumer<Message> observer);

    /**
     * Fuegt ein Observer hinzu welcher beim Unterbruch zum Server aufgerufen wird.
     * @param observer Der Observer welcher benachrichtigt werden soll beim Verbindungsunterbruch.
     */
    void addClientDisconnectedObserver(Runnable observer);
}
