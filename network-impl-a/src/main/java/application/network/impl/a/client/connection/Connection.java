package application.network.impl.a.client.connection;

import application.network.api.Message;

/**
 * Das Connection interface beschreibt alle oeffentlichen operationen einer Verbindung zum Server.
 */
public interface Connection {

    /**
     * Diese Methode sendet Nachrichten zum Server.
     * @param message Die Nachricht welche zum Server gesendet werden soll.
     */
    void send(Message message);

    /**
     * Schliest die Verbindung zum Server.
     */
    void shutdown();

    /**
     * Liest eine Nachricht synchron von der aktuellen Verbindung.
     * Diese Methode darf nur vor dem startReading Methodenaufruf verwendet werden.
     *
     * @return Die empfangene Nachricht.
     * @throws IllegalStateException Wenn die startReading Methode bereits aufgrufen wurde.
     * @throws InterruptedException Wenn der aktuelle Thread unterbrochen wurde.
     */
    Message read() throws InterruptedException;
}
