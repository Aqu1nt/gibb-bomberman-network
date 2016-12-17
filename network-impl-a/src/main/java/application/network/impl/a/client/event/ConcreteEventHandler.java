package application.network.impl.a.client.event;

import application.network.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Konkrete threadsichere Impelementierung des EventMangers.
 */
public class ConcreteEventHandler implements EventManager {

    private final Set<Consumer<Message>> messageObserver;
    private final Set<Runnable> serverDisconnectedObserver;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ConcreteEventHandler() {
        this.messageObserver = new ConcurrentSkipListSet<>();
        this.serverDisconnectedObserver = new ConcurrentSkipListSet<>();
    }

    /**
     * Benachrichtigt alle observer das eine neue Nachricht eingetroffen ist.
     *
     * @param message Die Nachricht welche empfangen wurde.
     */
    @Override
    public void fireMessageReceived(Message message) {
        messageObserver.forEach((observer) -> observer.accept(message));
        log.debug(String.format("%d observer ueber neue Nachricht informiert.", messageObserver.size()));
    }

    /**
     * Fuegt ein neuer Nachrichtenempfangs Observer dem EventManager hinzu.
     *
     * @param observer Der Observer welcher die Nachricht konsumiert.
     */
    @Override
    public void addMessageReceivedObserver(Consumer<Message> observer) {
        messageObserver.add(observer);
        log.trace("Neuer Nachrichten observer registriert.");
    }

    /**
     * Benachrichtigt alle Observer das die Serververbindung unterbrochen/beendet wurde.
     */
    @Override
    public void fireServerDisconnected() {
        serverDisconnectedObserver.forEach(Runnable::run);
        log.debug(String.format("%d observer ueber server disconnect informiert.", messageObserver.size()));
    }

    /**
     * Fuegt ein Observer hinzu welcher beim Unterbruch zum Server aufgerufen wird.
     *
     * @param observer Der Observer welcher benachrichtigt werden soll beim Verbindungsunterbruch.
     */
    @Override
    public void addClientDisconnectedObserver(Runnable observer) {
        serverDisconnectedObserver.add(observer);
        log.trace("Neuer Server disconnected observer registriert.");
    }
}
