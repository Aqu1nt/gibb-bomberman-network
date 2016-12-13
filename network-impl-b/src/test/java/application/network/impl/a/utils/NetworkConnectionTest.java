package application.network.impl.a.utils;

import application.network.api.Message;
import application.network.impl.a.TestWithServer;
import application.network.impl.a.internal.InternalMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.function.Consumer;

import static application.network.impl.a.utils.StaticHelpers.sleep;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest(InputStream.class)
public class NetworkConnectionTest extends TestWithServer
{
    @Test(timeout = 1000)
    public void open_should_start_listening_to_socket() throws IOException, ClassNotFoundException {
        NetworkConnection connection = spy(new NetworkConnection());
        connection.open(new Socket("localhost", PORT));
        sleep(150); // Ensure thread started
        serverOut.writeObject(mock(Message.class));
        sleep(150);
        verify(connection).dispatch(any(Message.class));
    }

    @Test
    public void should_dispach_messages_and_internal_messages_correctly()
    {
        NetworkConnection connection = new NetworkConnection();
        Consumer<Message> messageHandler = mock(Consumer.class);
        Consumer<InternalMessage> internalHandler = mock(Consumer.class);
        connection.handle(messageHandler);
        connection.handleInternal(internalHandler);

        InternalMessage internalMessage = mock(InternalMessage.class);
        Message message = mock(Message.class);
        connection.dispatch(internalMessage);
        connection.dispatch(message);

        verify(internalHandler).accept(internalMessage);
        verify(messageHandler).accept(message);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_when_connection_already_open() throws IOException
    {
        NetworkConnection connection = new NetworkConnection();
        connection.open(new Socket("localhost", PORT));
        connection.open(new Socket("localhost", PORT));
    }

    @Test(timeout = 1000)
    public void should_write_object_with_send_method() throws IOException, ClassNotFoundException
    {
        NetworkConnection connection = new NetworkConnection();
        connection.open(new Socket("localhost", PORT));
        connection.send(mock(Message.class));
        assertThat(serverIn.readObject(), is(instanceOf(Message.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void send_should_throw_exception_when_not_open()
    {
        NetworkConnection connection = new NetworkConnection();
        connection.send(mock(Message.class));
    }

    @Test(expected = NullPointerException.class)
    public void send_should_throw_exception_when_message_is_null()
    {
        NetworkConnection connection = new NetworkConnection();
        connection.send(null);
    }

    @Test
    public void should_close_itself_when_socket_closed() throws IOException
    {
        NetworkConnection connection = spy(new NetworkConnection());
        connection.open(new Socket("localhost", PORT));
        socket.close(); // Close remote socket
        sleep(100);
        verify(connection).close();
    }
}