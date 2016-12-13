package application.network.impl.a.client;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.impl.a.TestWithServer;
import application.network.impl.a.internal.InternalClientIdResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static application.network.impl.a.utils.StaticHelpers.sleep;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class BServerProxyTest extends TestWithServer
{
    private BServerProxy proxy;

    @Before
    public void create_proxy()
    {
        proxy = spy(BServerProxy.class);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_on_connect_when_already_connected() throws LobbyFullException, IOException, ClientIdInUseException
    {
        doNothing().when(proxy).verifyConnection();
        proxy.connect("1234", "localhost", PORT);
        proxy.connect("1234", "localhost", PORT);
    }

    @Test(timeout = 2000)
    public void connect_will_call_open_and_verify_the_connection() throws LobbyFullException, IOException, ClientIdInUseException
    {
        doNothing().when(proxy).verifyConnection();
        proxy.connect("1234", "localhost", PORT);
        verify(proxy).open(any());
        verify(proxy).verifyConnection();
    }

    @Test(expected = LobbyFullException.class, timeout = 2000)
    public void verify_connection_should_throw_lobby_full_exception_when_rejected() throws LobbyFullException, IOException, ClientIdInUseException
    {
        new Thread(() -> {
            sleep(250);
            proxy.handleClientIdResponse(new InternalClientIdResponse().setClientRejected(true));
        }).start();
        proxy.verifyConnection();
    }

    @Test(expected = ClientIdInUseException.class, timeout = 2000)
    public void verify_connection_should_client_id_in_use_exception_when_in_use() throws LobbyFullException, IOException, ClientIdInUseException
    {
        new Thread(() -> {
            sleep(250);
            proxy.handleClientIdResponse(new InternalClientIdResponse().setClientIdInUse(true));
        }).start();
        proxy.verifyConnection();
    }

    @Test(expected = IOException.class, timeout = 2000)
    public void should_timeout_when_server_not_responding() throws LobbyFullException, IOException, ClientIdInUseException
    {
        proxy.verifyConnection();
    }

    @Test
    public void should_call_disconnected_handlers_on_close()
    {
        Runnable handler = mock(Runnable.class);
        proxy.addServerDisconnectedHandler(handler);
        proxy.close();
        verify(handler).run();
    }

    @Test
    public void should_call_message_handler_on_incoming_message()
    {
        Message message = mock(Message.class);
        Consumer<Message> handler = mock(Consumer.class);
        proxy.addMessageHandler(handler);
        proxy.handleIncomingMessage(message);
        verify(handler).accept(message);
    }
}