package application.network.mock;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Test to verify {@link MockServerProxy} is working as expected.
 */
public class MockServerProxyTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MockServerProxy client = MockServerProxy.get();

    @Before
    public void resetMocks()
    {
        MockServerProxy.reset();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimulateMessage()
    {
        Consumer<Message> handler = mock(Consumer.class);
        Message message = mock(Message.class);
        client.addMessageHandler(handler);
        MockServerProxy.simulateMessage(message);
        verify(handler).accept(eq(message));
    }

    @Test
    public void testSimulateServerDisconnect()
    {
        Runnable handler = mock(Runnable.class);
        client.addServerDisconnectedHandler(handler);
        MockServerProxy.simulateServerDisconnect();
        verify(handler).run();
    }

    @Test
    public void testConnectSuccesful() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.connect("", "", 0);
    }

    @Test(expected = NullPointerException.class)
    public void testExceptionWhenConnectWithClientIdNull() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.connect(null, "", 0);
    }

    @Test(expected = NullPointerException.class)
    public void testExceptionWhenConnectWithIpNull() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.connect("", null, 0);
    }

    @Test
    public void testDisconnectWhileConnectedAndDisconnected() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.disconnect();
        client.connect("", "", 0);
        client.disconnect();
    }

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenSendWhileNotConnected() throws IOException
    {
        client.send(mock(Message.class));
        verify(client).send(any(Message.class));
    }

    @Test
    public void testSendWhenConnected() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.connect("", "", 0);
        client.send(mock(Message.class));
        thrown.expect(NullPointerException.class);
        client.send(null);
    }

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenSendingUnserializableMessage() throws IOException, LobbyFullException, ClientIdInUseException
    {
        class UnserializableMessage implements Message {
            private MockServerProxy client = MockServerProxyTest.this.client;
        }

        client.connect("", "", 0);
        client.send(new UnserializableMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExceptionWhenMessageHandlerNull()
    {
        client.addMessageHandler(mock(Consumer.class));
        thrown.expect(NullPointerException.class);
        client.addMessageHandler(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExceptionWhenDisconnectHandlerNull()
    {
        client.addServerDisconnectedHandler(mock(Runnable.class));
        thrown.expect(NullPointerException.class);
        client.addServerDisconnectedHandler(null);
    }

    @Test
    public void testRestart() throws LobbyFullException, IOException, ClientIdInUseException
    {
        client.connect("", "", 0);
        client.disconnect();
        client.connect("", "", 0);
    }

    @Test
    public void testSimulateLobbyFull() throws LobbyFullException, IOException, ClientIdInUseException
    {
        MockServerProxy.simulateLobbyFull();
        MockServerProxy.simulateLobbyEmpty();
        client.connect("", "", 0);
        client.disconnect();
        MockServerProxy.simulateLobbyFull();
        thrown.expect(LobbyFullException.class);
        client.connect("", "", 0);
    }

    @Test
    public void testSimulateClientIdInUse() throws LobbyFullException, IOException, ClientIdInUseException
    {
        MockServerProxy.simulateClientIdInUse();
        MockServerProxy.simulateClientIdFree();
        client.connect("", "", 0);
        client.disconnect();
        MockServerProxy.simulateClientIdInUse();
        thrown.expect(ClientIdInUseException.class);
        client.connect("", "", 0);
    }
}
