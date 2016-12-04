package application.network.impl.a.client.connection;

import application.network.api.Message;

/**
 * Dieses Interface beschreibt die öffentliche Schnittstelle eines Verbindungsbetreuers.
 */
public interface ConnectionHandler {

    /**
     * Diese Methode wird aufgerufen wenn eine Verbindung erstellt wurde.
     * Zu diesem Zeitpunkt ist der Leseprozess noch nicht gestartet daher sind synchrone Lesezugriffe moeglich.
     * Der Leseprozess wird automatisch gestartet wenn diese Methode keine Ausnahme generiert.
     *
     * @param serverConnection Die neu erzeugte Server Verbindung.
     *
     * @throws application.network.api.client.LobbyFullException        Wenn der Server die Anmeldung abgelehnt hat weil keine Lobby mehr Platz hat.
     * @throws application.network.api.client.ClientIdInUseException    Wenn ein Client bereits mit der selben Id angemeldet ist und der Server die neue Verbindung aus diesem Grund ablehnt.
     * @throws IllegalStateException Wenn diese Methode aufgerufen wurde wenn bereits eine Clientverbindung aufgebaut ist.
     */
    void connectionCreated(Connection serverConnection);

    /**
     * Diese Methode wird aufgerufen wenn der Client eine neue Nachricht von dem Server erhalten hat.
     * @param message Die neu erhaltene Nachricht.
     */
    void messageReceived(Message message);

    /**
     * Diese Methode wird aufgerufen wenn eine Nachricht zum Server gesendet wurde.
     *
     * @param message Die Nachricht welche gesendet wurde.
     */
    void messageSend(Message message);

    /**
     * Diese Methode wird aufgrerufen wenn die aktuelle Verbindung in einem der lesenden oder schreibenden Threads
     * eine nicht behandelte Ausnahme generiert. Diese Methode darf keine Ausnahmen generieren da diese von dem unhandelt exception handler der jvm aufgerufen wird.
     * Da die Verbindung zu diesem Zeitpunkt nicht mehr in einem definierten Zustand ist muss diese korrekt gschlossen werden.
     *
     * @param ex Die Ausname welche generiert wurde.
     */
    void exceptionThrown(Exception ex);

    /**
     * Diese Methode wird aufgerufen wenn die aktuelle Verbindung erfolgreich geschlossen wurde.
     */
    void connectionClosed();

    /**
     * Sendet eine Nachricht an den Server.
     * Dieser Vorgang wird asynchron behandlet.
     *
     * @param message Die Nachricht welche gesendet werden soll.
     * @throws NullPointerException Wenn die Nachricht null ist.
     */
    void send(Message message);
}
