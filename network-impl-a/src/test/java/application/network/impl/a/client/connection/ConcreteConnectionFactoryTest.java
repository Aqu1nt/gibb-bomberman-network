package application.network.impl.a.client.connection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by nschaefli on 12/4/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConcreteConnectionFactoryTest {

    @Mock
    private Socket socket;

    @Mock
    private ConnectionHandler handler;

    private ConcreteConnectionFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new ConcreteConnectionFactory(handler);
    }

    @Test
    public void testCreateWhichShouldSucceed() throws Exception {

        //pipe internal magic zu ObjectInputStream da dieser den aktuellen Thread blockiert ohne den header welcher der ObjectOutputStream versendet.
        PipedOutputStream pout = new PipedOutputStream();
        PipedInputStream pin = new PipedInputStream(pout);
        ObjectOutputStream out = new ObjectOutputStream(pout);

        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(socket.getInputStream()).thenReturn(pin);
        when(socket.isConnected()).thenReturn(true);

        Connection connection = factory.create(socket);

        assertNotNull("Die Verbindung darf nicht null sein.", connection);
        assertTrue("Die Verbindung muss von dem typ ConcreteConnection sein.", connection instanceof ConcreteConnection);
        verify(handler, times(1)).connectionCreated(connection);
        assertTrue("Verbindung muss im lesenden zustand sein.", ((ConcreteConnection) connection).isReading());

        connection.shutdown();
        verify(handler, times(1)).connectionClosed();
        verifyNoMoreInteractions(handler);

        //cleanup
        out.close();
        pout.close();
        pin.close();
    }

}