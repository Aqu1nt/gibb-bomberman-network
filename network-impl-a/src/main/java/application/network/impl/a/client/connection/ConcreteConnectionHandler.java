package application.network.impl.a.client.connection;

import application.network.api.Message;
import application.network.impl.a.client.event.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Konkreter Verbindungsprozessor.
 */
public class ConcreteConnectionHandler implements ConnectionHandler {

    private EventDispatcher eventDispatcher;
    private Connection connection;
    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * ConcreteConenectionHandler constructor.
     * @param eventDispatcher The event dispatcher which should be used by this handler.
     */
    public ConcreteConnectionHandler(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Diese Methode wird aufgerufen wenn eine Verbindung erstellt wurde.
     * Zu diesem Zeitpunkt ist der Leseprozess noch nicht gestartet daher sind synchrone Lesezugriffe moeglich.
     * Der Leseprozess wird automatisch gestartet wenn diese Methode keine Ausnahme generiert.
     *
     * @param serverConnection Die neu erzeugte Server Verbindung.
     * @throws IllegalStateException  Wenn diese Methode aufgerufen wurde wenn bereits eine Clientverbindung aufgebaut ist.
     */
    @Override
    public void connectionCreated(Connection serverConnection) {

        this.connection = serverConnection;
    }

    /**
     * Diese Methode wird aufgerufen wenn der Client eine neue Nachricht von dem Server erhalten hat.
     *
     * @param message Die neu erhaltene Nachricht.
     * @throws NullPointerException Wenn die Nachricht null ist.
     */
    @Override
    public void messageReceived(Message message) {
        eventDispatcher.fireMessageReceived(message);
        log.debug("Nachricht erhalten: " + message.getClass());
    }

    /**
     * Diese Methode wird aufgerufen wenn eine Nachricht zum Server gesendet wurde.
     *
     * @param message Die Nachricht welche gesendet wurde.
     */
    @Override
    public void messageSend(Message message) {
        log.debug("Nachricht gesendet: " + message.getClass());
    }

    /**
     * Diese Methode wird aufgrerufen wenn die aktuelle Verbindung in einem der lesenden oder schreibenden Threads
     * eine nicht behandelte Ausnahme generiert. Diese Methode darf keine Ausnahmen generieren da diese von dem unhandelt exception handler der jvm aufgerufen wird.
     * Da die Verbindung zu diesem Zeitpunkt nicht mehr in einem definierten Zustand ist muss diese korrekt gschlossen werden.
     *
     * @param ex Die Ausname welche generiert wurde.
     */
    @Override
    public void exceptionThrown(Exception ex) {
        log.error("Unexpected exception thrown in connection.", ex);
        if(connection != null)
        {
            log.info("Connection auto closed by the connection handler to prevent leaking of the underlying socket.");
            connection.shutdown();
            connection = null;
        }
    }

    /**
     * Diese Methode wird aufgerufen wenn die aktuelle Verbindung erfolgreich geschlossen wurde.
     * Es wird nur ein server disconnected event ausgeloest wenn eine Verbindung registriert wurde.
     */
    @Override
    public void connectionClosed() {
        if(connection != null)
            eventDispatcher.fireServerDisconnected();
        connection = null;
    }

    /**
     * Sendet eine Nachricht an den Server.
     * Dieser Vorgang wird asynchron behandlet.
     *
     * @param message Die Nachricht welche gesendet werden soll.
     * @throws NullPointerException Wenn die Nachricht null ist.
     * @throws IllegalStateException Wenn keine Verbindung zum Server besteht.
     */
    @Override
    public void send(Message message) {
        if(connection != null)
            connection.send(message);
        else
            throw new IllegalStateException("Keine Verbindung zum Server verfügbar.");
    }

    /**
     * Schliesst die aktuelle Verbindung zum Server.
     */
    @Override
    public void shutdown() {
        if(connection != null)
            connection.shutdown();
        else
            throw new IllegalStateException("Keine Verbindung zum Server vorhanden, welche geschlossen werden koennte.");
    }
}
