package application.network.mock;

import application.network.api.Message;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Test to verify {@link MockServer} is working as expected
 */
public class MockServerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private MockServer server = MockServer.get();
    
    @Before
    public void resetMocks()
    {
        MockServer.reset();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimulateMessage()
    {
        Message message = mock(Message.class);
        BiConsumer<Message, String> handler = mock(BiConsumer.class);
        server.addMessageHandler(handler);
        MockServer.simulateMessage(message, "client1");
        verify(handler).accept(eq(message), eq("client1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimulateClientDisconnect()
    {
        Consumer<String> handler = mock(Consumer.class);
        server.addClientDisconnectedHandler(handler);
        MockServer.simulateClientDisconnect("client2");
        verify(handler).accept("client2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimulateClientConnect()
    {
        Consumer<String> handler = mock(Consumer.class);
        server.addClientConnectedHandler(handler);
        MockServer.simulateClientConnect("client3");
        verify(handler).accept("client3");
    }

    @Test
    public void testStartServerSuccessful() throws IOException
    {
        server.listen(1000);
    }

    @Test
    public void testStartServerTwoTimesShouldFail() throws IOException
    {
        server.listen(1000);
        thrown.expect(IllegalStateException.class);
        server.listen(1000);
    }

    @Test
    public void testShutdownServerStartedAndUnstarted() throws IOException
    {
        server.shutdown();
        server.listen(1000);
        server.shutdown();
    }

    @Test(expected = IOException.class)
    public void testExceptionWhenSendingMessageWhenStopped() throws IOException
    {
        server.send(mock(Message.class), "");
    }
    
    @Test
    public void testSendMessageWhenServerStarted() throws IOException
    {
        server.listen(100);
        server.send(mock(Message.class), "");
    }
    
    @Test(expected = IOException.class)
    public void testExceptionWhenBroadcastingWithoutServerStarted() throws IOException
    {
        server.broadcast(mock(Message.class));
    }

    @Test
    public void testBroadcastWhenServerStarted() throws IOException
    {
        server.listen(1000);
        server.broadcast(mock(Message.class));
    }

    @Test(expected = NullPointerException.class)
    public void testExceptionWhenSendMessageNull() throws IOException
    {
        server.send(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testExceptionWhenSendClientIdNull() throws IOException
    {
        server.send(mock(Message.class), null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExceptionWhenAddMessageHandlerNull()
    {
        server.addMessageHandler(mock(BiConsumer.class));
        thrown.expect(NullPointerException.class);
        server.addMessageHandler(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExceptionWhenAddDisconnectHandlerNull()
    {
        server.addClientDisconnectedHandler(mock(Consumer.class));
        thrown.expect(NullPointerException.class);
        server.addClientDisconnectedHandler(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExceptionWhenAddConnectHandlerNull()
    {
        server.addClientConnectedHandler(mock(Consumer.class));
        thrown.expect(NullPointerException.class);
        server.addClientConnectedHandler(null);
    }

    @Test
    public void testRestart() throws IOException
    {
        server.listen(100);
        server.shutdown();
        server.listen(100);
    }
}
