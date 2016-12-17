package application.network.impl.a.client;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.impl.a.client.connection.Connection;
import application.network.impl.a.client.connection.ConnectionFactory;
import application.network.impl.a.client.connection.ConnectionHandler;
import application.network.impl.a.client.event.EventManager;
import application.network.impl.a.message.ClientLoginRequest;
import application.network.impl.a.message.InternalMessage;
import application.network.impl.a.message.LoginFailedMessage;
import application.network.impl.a.message.LoginSuccessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Konkrete Client Im­ple­men­tie­rung.
 */
public class ConcreteClient implements ServerProxy {

    private ConnectionFactory connectionFactory;
    private ConnectionHandler connectionHandler;
    private EventManager eventManager;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ConcreteClient(ConnectionFactory connectionFactory, ConnectionHandler connectionHandler, EventManager eventManager) {
        this.connectionFactory = connectionFactory;
        this.connectionHandler = connectionHandler;
        this.eventManager = eventManager;
    }

    /**
     * Verbindet den Client auf den Ziel Server
     *
     * @param clientId die Id dieses Clients
     * @param ip       die Ziel IP
     * @param port     der Ziel Port
     * @throws IOException            wenn die Verbindung fehlschlägt
     * @throws ClientIdInUseException wenn die gegebene client id bereits exisitert auf dem server
     * @throws LobbyFullException     wenn die Lobby keine weiteren Spieler zulässt
     * @throws NullPointerException   wenn die clientId oder die ip null ist
     */
    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException {

        //TODO: refactor method connect.
        log.trace(String.format("Verbinde zu %s:%d mit der client id: %s", ip, port, clientId));
        Socket serverConnection = new Socket(ip, port);
        Connection connection = connectionFactory.create(serverConnection);

        sendAuthRequestToServer(connection, clientId);
        Message authAnswer = awaitServerLoginAnswer(connection);

        if(!isInternalMessage(authAnswer))
        {
            log.error("Ungueltiger Handshake.");
            connection.shutdown();
            throw new IOException("Ungueltiger Antwort von Server erhalten. Verwenden der Server auch die Network Team A implementation ?");
        }

        if(authAnswer instanceof LoginSuccessMessage)
        {
            log.info("Serververbindung erfolgreich erstellt.");
            connectionHandler.connectionCreated(connection);
            connection.startReading();
            return;
        }

        if(authAnswer instanceof LoginFailedMessage)
        {
            LoginFailedMessage message = (LoginFailedMessage) authAnswer;

            if(message.getReason().equals(LoginFailedMessage.LoginFailedReason.LOBBY_FULL))
            {
                log.error("Serververbindung konnte nicht erstellt werden, da die Lobby voll ist.");
                connection.shutdown();
                throw new LobbyFullException();
            }

            if(message.getReason().equals(LoginFailedMessage.LoginFailedReason.ACCESS_DENIED))
            {
                log.error("Serververbindung konnte nicht erstellt werden, da der Server die Verbindung abgelehnt hat.");
                connection.shutdown();
                throw new IOException("Serververbindung konnte nicht erstellt werden, da der Server die Verbindung abgelehnt hat.");
            }

            if(message.getReason().equals(LoginFailedMessage.LoginFailedReason.NAME_ALREADY_USED))
            {
                log.error("Serververbindung konnte nicht erstellt werden, da die Client ID bereits verwendet wird.");
                connection.shutdown();
                throw new ClientIdInUseException();
            }
        }

        connection.shutdown();
        log.error("Unbekannte interne Nachricht erhalten. Verwendet der Server eine neuere Version des Netzwerkteams A?");
        throw new IOException("Unbekannte interne Nachricht erhalten. Verwendet der Server eine neuere Version des Netzwerkteams A?");
    }

    /**
     * Schliesst die Verbindung zum Server
     */
    @Override
    public void disconnect() {
        connectionHandler.shutdown();
    }

    /**
     * Sendet ein Nachrichtenobjekt an den Server. Diese Methode muss innerhalb der Netzwerkschicht
     * implementiert werden.
     *
     * @param message Das Nachrichtenobjekt, welches an den Server gesendet werden soll.
     * @throws NullPointerException wenn die Message null ist
     */
    @Override
    public void send(Message message) {
        connectionHandler.send(message);
    }

    /**
     * Registriert einen Handler welcher mit jeder eingehenden Nachricht
     * aufgerufen wird
     *
     * @param handler der Handler
     * @throws NullPointerException wenn der handler null ist
     */
    @Override
    public void addMessageHandler(Consumer<Message> handler) {
        eventManager.addMessageReceivedObserver(handler);
    }

    /**
     * Registriert einen Handler welcher in jedem Fall aufgerufen wird wenn
     * die Socketverbindung zum Server geschlossen wird
     *
     * @param handler der Handler
     * @throws NullPointerException wenn der handler null ist
     */
    @Override
    public void addServerDisconnectedHandler(Runnable handler) {
        eventManager.addClientDisconnectedObserver(handler);
    }

    /**
     * Ueberprueft ob die Nachricht eine interen Nachricht ist.
     *
     * @param message   Die Nachricht welche geprüft werden soll.
     * @return true wenn die Nachricht intern ist und ansonsten false.
     */
    private boolean isInternalMessage(Message message)
    {
        return message instanceof InternalMessage;
    }


    /**
     * Diese Methode wartet bis die Anmeldeantwort vom Server empfangen wurde.
     *
     * @param connection    Die Verbindung welche verwendet wereden soll.
     * @return Die Nachricht welche empfangen wurde.
     * @throws IOException Wenn der Lesevorgang fehlschlaegt.
     */
    private Message awaitServerLoginAnswer(Connection connection) throws IOException
    {
        try
        {
            log.trace("Warte auf Antwort von Server.");
            return connection.read();
        }
        catch (InterruptedException ex)
        {
            log.error("Verbindungsaufbau wurde unterbrochen.");
            throw new IOException("Verbindungsaufau wurde unterbrochen.", ex);
        }
    }

    /**
     * Sendet eine Login Anfrage zum Server.
     *
     * @param connection    Die Verbindung welche zum Server aufgebaut wurde.
     * @param clientId      Die Client ID welche der Server verwenden soll.
     */
    private void sendAuthRequestToServer(Connection connection, String clientId)
    {
        log.trace("Sende auth request.");
        ClientLoginRequest loginRequest = new ClientLoginRequest();
        loginRequest.setClientId(clientId);

        connection.send(loginRequest);
    }
}
