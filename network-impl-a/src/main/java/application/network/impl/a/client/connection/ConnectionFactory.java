package application.network.impl.a.client.connection;

import java.io.IOException;
import java.net.Socket;

/**
 * Beschreibt das oeffentliche interface der ConnectionFactories.
 * Welche die konkreten Verbindungen herstellen.
 */
public interface ConnectionFactory {

    /**
     * Kappselt die uebergebene Serververbindung in eine Hilfsklasse welche
     * das Senden und Empfangen von Nachrichten vereinfacht.
     *
     * @param serverConnection Die aufgebaute Serververbindung.
     * @return Die neu erzeugte Instanz der Hilfsklasse.
     *
     * @throws IOException Wenn keine Verbindung erstellt werden konnte.
     */
    Connection create(Socket serverConnection) throws IOException;
}
