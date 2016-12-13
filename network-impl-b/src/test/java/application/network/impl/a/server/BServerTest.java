package application.network.impl.a.server;

import application.network.api.Message;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
public class BServerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BServer server = new BServer();

    @After
    public void shutdownServerAfterTest()
    {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test(timeout = 1000)
    public void server_listen_should_open_port_and_not_block() throws IOException
    {
        server = new BServer();
        server.listen(50000);

        new Socket("localhost", 50000);
        new Socket("localhost", 50000);
        new Socket("localhost", 50000);
    }

    @Test
    public void server_should_throw_exception_when_already_running() throws IOException
    {
        server = new BServer();
        server.listen(50000);
        thrown.expect(IllegalStateException.class);
        server.listen(50000);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void server_should_create_new_server_connections_on_connect() throws IOException
    {
        BiFunction<Socket, BServer, BServerConnection> mockFactory = mock(BiFunction.class);
        server = new BServer(mockFactory);
        server.listen(50000);

        new Socket("localhost", 50000);
        verify(mockFactory).apply(any(), eq(server));
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_on_connection_with_existing_client_id()
    {
        BServerConnection connection = mock(BServerConnection.class);
        given(connection.getClientId()).willReturn("1234");
        BServerConnection connection1 = mock(BServerConnection.class);
        given(connection1.getClientId()).willReturn("1234");

        server.registerConnection(connection);
        server.registerConnection(connection1);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_on_connection_without_client_id()
    {
        BServerConnection connection = mock(BServerConnection.class);
        server.registerConnection(connection);
    }

    @Test
    public void client_id_should_not_be_in_use_without_connections()
    {
        assertThat(server.isClientIdInUse("hoi"), is(false));
    }

    @Test(expected = NullPointerException.class)
    public void client_id_in_use_should_throw_nullpointerexception()
    {
        server.isClientIdInUse(null);
    }

    @Test
    public void client_id_should_be_in_use_when_connection_with_same_name_exists()
    {
        BServerConnection connection = mock(BServerConnection.class);
        given(connection.getClientId()).willReturn("1234");
        server.registerConnection(connection);
        assertThat(server.isClientIdInUse("1234"), is(true));
        assertThat(server.isClientIdInUse("4321"), is(false));
    }

    @Test
    public void client_id_should_not_be_in_use_after_unregister()
    {
        BServerConnection connection = mock(BServerConnection.class);
        given(connection.getClientId()).willReturn("1234");
        server.registerConnection(connection);
        assertThat(server.isClientIdInUse("1234"), is(true));
        server.unregisterConnection(connection);
        assertThat(server.isClientIdInUse("1234"), is(false));
    }

    @Test
    public void should_accept_client_id_without_handlers()
    {
        assertThat(server.isClientAccepted("1234"), is(true));
    }

    @Test
    public void should_accept_client_id_when_all_handlers_return_true()
    {
        server.addClientConnectedHandler(str -> true);
        assertThat(server.isClientAccepted("1234"), is(true));
        server.addClientConnectedHandler(str -> true);
        assertThat(server.isClientAccepted("1234"), is(true));
    }

    @Test
    public void should_deny_client_id_when_one_handler_returns_false()
    {
        server.addClientConnectedHandler(str -> false);
        assertThat(server.isClientAccepted("1234"), is(false));
        server.addClientConnectedHandler(str -> true);
        assertThat(server.isClientAccepted("1234"), is(false));
    }

    @Test
    public void should_call_disconnected_handler_on_unregister()
    {
        Consumer<String> handler = mock(Consumer.class);
        BServerConnection connection = mock(BServerConnection.class);
        given(connection.getClientId()).willReturn("1234");

        server.addClientDisconnectedHandler(handler);
        server.unregisterConnection(connection); // This shouldnt do anything
        verify(handler, never()).accept("1234");

        server.registerConnection(connection);
        server.unregisterConnection(connection);
        verify(handler).accept("1234");
    }

    @Test
    public void should_call_message_handlers_on_message_from_registered_connection()
    {
        BServerConnection connection = mock(BServerConnection.class);
        BiConsumer<Message, String> handler1 = mock(BiConsumer.class);
        BiConsumer<Message, String> handler2 = mock(BiConsumer.class);
        Message message = mock(Message.class);
        server.addMessageHandler(handler1);
        server.addMessageHandler(handler2);
        given(connection.getClientId()).willReturn("1234");

        server.handleMessage(connection, message);
        verify(handler1, never()).accept(message, "1234");
        verify(handler2, never()).accept(message, "1234");

        server.registerConnection(connection);
        server.handleMessage(connection, message);
        verify(handler1, only()).accept(message, "1234");
        verify(handler2, only()).accept(message, "1234");
    }

    @Test
    public void send_should_forward_message_to_correct_connection()
    {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        BServerConnection connection1 = mock(BServerConnection.class);
        given(connection1.getClientId()).willReturn("1234");
        server.registerConnection(connection1);
        BServerConnection connection2 = mock(BServerConnection.class);
        given(connection2.getClientId()).willReturn("4321");
        server.registerConnection(connection2);

        server.send(message1, "1234");
        server.send(message2, "4321");
        server.send(message1, "0000");
        server.send(message2, "0000");
        verify(connection1).send(message1);
        verify(connection1, never()).send(message2);
        verify(connection2).send(message2);
        verify(connection2, never()).send(message1);
    }

    @Test
    public void broadcast_should_forward_message_to_all_connections()
    {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        BServerConnection connection1 = mock(BServerConnection.class);
        given(connection1.getClientId()).willReturn("1234");
        server.registerConnection(connection1);
        BServerConnection connection2 = mock(BServerConnection.class);
        given(connection2.getClientId()).willReturn("4321");
        server.registerConnection(connection2);

        server.broadcast(message1);
        server.broadcast(message2);
        verify(connection1).send(message1);
        verify(connection1).send(message2);
        verify(connection2).send(message1);
        verify(connection2).send(message2);
    }

}