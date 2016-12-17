package application.network.impl.a.client.connection;

import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.impl.a.client.ConcreteClient;
import application.network.impl.a.client.event.EventManager;
import application.network.impl.a.internalMessages.ClientLoginRequest;
import application.network.impl.a.internalMessages.LoginFailedMessage;
import application.network.impl.a.internalMessages.LoginSuccessMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConcreteClientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private EventManager eventManager;

    @Mock
    private ConnectionHandler connectionHandler;

    @InjectMocks
    private ConcreteClient client;

    private final int testServerPort = 9090;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConnectWithSuccessfulLoginWhichShouldSucceed() throws Exception {

        Connection connection = mock(Connection.class);
        when(connectionFactory.create(any(Socket.class))).thenReturn(connection);
        when(connection.read()).thenReturn(new LoginSuccessMessage());

        Future<Socket> futureClientConnection = connectToClient();
        client.connect("testclient1", "127.0.0.1", testServerPort);
        Socket clientConnection = futureClientConnection.get(1, TimeUnit.SECONDS);

        assertTrue(clientConnection.isConnected());
        verify(connection, times(1)).send(any(ClientLoginRequest.class));
        verify(connection, times(1)).read();
        verify(connection, times(1)).startReading();
        verify(connectionHandler, times(1)).connectionCreated(connection);
        verify(connectionFactory, times(1)).create(any(Socket.class));

        verifyNoMoreInteractions(connection);
        verifyNoMoreInteractions(connectionFactory);
        verifyNoMoreInteractions(eventManager);
        verifyNoMoreInteractions(connectionHandler);
    }

    @Test
    public void testConnectWithFailedLoginReasonLobbyFullWhichShouldFail() throws Exception {

        Connection connection = mock(Connection.class);
        when(connectionFactory.create(any(Socket.class))).thenReturn(connection);

        LoginFailedMessage message = new LoginFailedMessage();
        message.setReason(LoginFailedMessage.LoginFailedReason.LOBBY_FULL);
        when(connection.read()).thenReturn(message);

        connectToClient();

        expectedException.expect(LobbyFullException.class);
        client.connect("testclient1", "127.0.0.1", testServerPort);
    }

    @Test
    public void testConnectWithFailedLoginReasonAccessDeniedWhichShouldFail() throws Exception {

        Connection connection = mock(Connection.class);
        when(connectionFactory.create(any(Socket.class))).thenReturn(connection);

        LoginFailedMessage message = new LoginFailedMessage();
        message.setReason(LoginFailedMessage.LoginFailedReason.ACCESS_DENIED);
        when(connection.read()).thenReturn(message);

        connectToClient();

        expectedException.expect(IOException.class);
        expectedException.expectMessage("Serververbindung konnte nicht erstellt werden, da der Server die Verbindung abgelehnt hat.");
        client.connect("testclient1", "127.0.0.1", testServerPort);
    }

    @Test
    public void testConnectWithFailedLoginReasonNameAlreadyUsedWhichShouldFail() throws Exception {

        Connection connection = mock(Connection.class);
        when(connectionFactory.create(any(Socket.class))).thenReturn(connection);

        LoginFailedMessage message = new LoginFailedMessage();
        message.setReason(LoginFailedMessage.LoginFailedReason.NAME_ALREADY_USED);
        when(connection.read()).thenReturn(message);

        connectToClient();

        expectedException.expect(ClientIdInUseException.class);
        client.connect("testclient1", "127.0.0.1", testServerPort);
    }

    /**
     * Verbindet sich asynchron mit dem ersten client und schliesst den server socket wieder.
     * @return Die zukuenftige Verbindung zum client.
     * @throws Exception Wenn was schief laeuft.
     */
    private Future<Socket> connectToClient() throws Exception
    {
        Future<Socket> clientConnection = CompletableFuture.supplyAsync(() -> {

            try(ServerSocket serverSocket = new ServerSocket(testServerPort))
            {
                return serverSocket.accept();
            }
            catch(IOException ex)
            {
                throw new RuntimeException("test server failed please check the concrete client unit tests.");
            }

        });

        return  clientConnection;
    }

}