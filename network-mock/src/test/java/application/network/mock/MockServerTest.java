package application.network.mock;

import application.network.api.Message;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        Function<String, Boolean> handler = mock(Function.class);
        when(handler.apply(anyString())).thenReturn(true);
        server.addClientConnectedHandler(handler);
        assertThat(MockServer.simulateClientConnect("client3"), is(true));
        verify(handler).apply("client3");

        Function<String, Boolean> handler1 = id -> false;
        Function<String, Boolean> handler2 = id -> true;
        MockServer.reset();
        server.addClientConnectedHandler(handler1);
        server.addClientConnectedHandler(handler2);
        assertThat(MockServer.simulateClientConnect("client"), is(false));
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

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenSendingMessageWhenStopped()
    {
        server.send(mock(Message.class), "");
    }
    
    @Test
    public void testSendMessageWhenServerStarted() throws IOException
    {
        server.listen(100);
        server.send(mock(Message.class), "client");
        verify(server).send(any(Message.class), eq("client"));
    }

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenSendingUnserializableMessage() throws IOException
    {
        class UnserializableMessage implements Message {
            private MockServer server = MockServerTest.this.server;
        }

        server.listen(1000);
        server.send(new UnserializableMessage(), "client5");
    }

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenBroadcastingWithoutServerStarted() throws IOException
    {
        server.broadcast(mock(Message.class));
    }

    @Test
    public void testBroadcastWhenServerStarted() throws IOException
    {
        server.listen(1000);
        server.broadcast(mock(Message.class));
        verify(server).broadcast(any(Message.class));
    }

    @Test(expected = UncheckedIOException.class)
    public void testExceptionWhenBroadcastUnserializableMessage() throws IOException
    {
        class UnserializableMessage implements Message {
            private MockServer server = MockServerTest.this.server;
        }

        server.listen(1000);
        server.broadcast(new UnserializableMessage());
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
        server.addClientConnectedHandler(mock(Function.class));
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
