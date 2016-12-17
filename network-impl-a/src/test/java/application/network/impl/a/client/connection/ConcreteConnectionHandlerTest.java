package application.network.impl.a.client.connection;

import application.network.api.Message;
import application.network.impl.a.client.event.ConcreteEventHandler;
import application.network.impl.a.message.LoginSuccessMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConcreteConnectionHandlerTest {

    @Mock
    private ConcreteEventHandler eventHandler;

    private ConcreteConnectionHandler concreteConnectionHandler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        concreteConnectionHandler = new ConcreteConnectionHandler(eventHandler);
        concreteConnectionHandler = spy(concreteConnectionHandler);
    }

    @Test
    public void testSendMessageWhichShouldSucceed() throws Exception
    {
        Connection connection = mock(Connection.class);
        this.concreteConnectionHandler.connectionCreated(connection);

        Message message = new LoginSuccessMessage();
        this.concreteConnectionHandler.send(message);
        verifyNoMoreInteractions(eventHandler);

        Thread.sleep(100);
        verify(connection, times(1)).send(message);
    }

    @Test
    public void testSendMessageWithoutConnectionWhichShouldFail() throws Exception
    {
        Message message = new LoginSuccessMessage();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Keine Verbindung zum Server verfügbar.");

        this.concreteConnectionHandler.send(message);

        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testShutdownWhichShouldSucceed() throws Exception
    {
        Connection connection = mock(Connection.class);
        this.concreteConnectionHandler.connectionCreated(connection);

        this.concreteConnectionHandler.shutdown();
        verifyNoMoreInteractions(eventHandler);

        Thread.sleep(100);
        verify(connection, times(1)).shutdown();
    }

    @Test
    public void testShutdownWithoutConnectionWhichShouldFail() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Keine Verbindung zum Server vorhanden, welche geschlossen werden koennte.");

        this.concreteConnectionHandler.shutdown();

        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testConnectionClosedWhichShouldSucceed() throws Exception
    {
        Connection connection = mock(Connection.class);
        this.concreteConnectionHandler.connectionCreated(connection);

        this.concreteConnectionHandler.connectionClosed();

        verify(eventHandler, times(1)).fireServerDisconnected();
        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testMessageReceivedWhichShouldSucceed() throws Exception
    {
        Connection connection = mock(Connection.class);
        this.concreteConnectionHandler.connectionCreated(connection);

        Message message = new LoginSuccessMessage();
        this.concreteConnectionHandler.messageReceived(message);

        verify(eventHandler, times(1)).fireMessageReceived(message);
        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testMessageReceivedWithNullPointerWhichShouldFail() throws Exception
    {
        expectedException.expect(NullPointerException.class);

        this.concreteConnectionHandler.messageReceived(null);
        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testExceptionThrownWhichShouldSucceed() throws Exception
    {
        Connection connection = mock(Connection.class);
        this.concreteConnectionHandler.connectionCreated(connection);

        this.concreteConnectionHandler.exceptionThrown(new IOException("test exception"));

        verifyNoMoreInteractions(eventHandler);
        verify(connection, times(1)).shutdown();
    }

    @Test
    public void testExceptionThrownWithNullConnectionWhichShouldSucceed() throws Exception
    {
        this.concreteConnectionHandler.exceptionThrown(new IOException("test exception"));
        verifyNoMoreInteractions(eventHandler);
    }

    @Test
    public void testExceptionThrownWithNullExceptionWhichShouldSucceed() throws Exception
    {
        this.concreteConnectionHandler.exceptionThrown(null);
        verifyNoMoreInteractions(eventHandler);
    }
}