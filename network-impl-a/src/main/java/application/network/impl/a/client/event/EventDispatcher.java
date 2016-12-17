package application.network.impl.a.client.event;

import application.network.api.Message;

/**
 * Dieses Interface definiert den lesenden Zugriff auf den EventManager.
 */
public interface EventDispatcher {

    /**
     * Benachrichtigt alle observer das eine neue Nachricht eingetroffen ist.
     * @param message   Die Nachricht welche empfangen wurde.
     */
    void fireMessageReceived(Message message);

    /**
     * Benachrichtigt alle Observer das die Serververbindung unterbrochen/beendet wurde.
     */
    void fireServerDisconnected();
}
