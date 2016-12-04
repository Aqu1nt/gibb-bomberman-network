package application.network.impl.a.client.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by nschaefli on 12/4/16.
 */
public class ConcreteConnectionFactory implements ConnectionFactory  {

    private final ConnectionHandler handler;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ConcreteConnectionFactory(ConnectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Kappselt die uebergebene Serververbindung in eine Hilfsklasse welche
     * das Senden und Empfangen von Nachrichten vereinfacht.
     *
     * @param serverConnection Die aufgebaute Serververbindung.
     * @return Die neu erzeugte Instanz der Hilfsklasse.
     */
    @Override
    public Connection create(Socket serverConnection) throws IOException{
        try
        {
            log.trace("Erstelle neue Verbindungswrapper.");
            ConcreteConnection concreteConnection = new ConcreteConnection(serverConnection, handler);

            log.debug("Rufe den connectionCreated event auf.");
            handler.connectionCreated(concreteConnection);

            log.trace("Starte Lesevorgang.");
            concreteConnection.startReading();
            return concreteConnection;
        }
        catch (IOException ex)
        {
            log.error("Erstellen der Verbindung war nicht moeglich.");
            throw new IOException("Konnte keine Connection erstellen für den übergebenen Socket.", ex);
        }
    }
}
