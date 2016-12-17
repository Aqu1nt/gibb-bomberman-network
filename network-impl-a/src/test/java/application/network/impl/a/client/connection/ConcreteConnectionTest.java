package application.network.impl.a.client.connection;

import application.network.api.Message;
import application.network.impl.a.internalMessages.LoginFailedMessage;
import application.network.impl.a.internalMessages.LoginSuccessMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ConcreteConnection Testklasse.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConcreteConnectionTest {

    @Mock
    private Socket socket;

    @Mock
    private ConnectionHandler handler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConcreteConnection connection;

    private ObjectOutput out;
    private ObjectInput in;

    //input pipe
    private PipedInputStream inputPIn;
    private PipedOutputStream inputPOut;

    //output pipe
    private PipedInputStream outputPIn;
    private PipedOutputStream outputPOut;

    @Before
    public void setUp() throws Exception {
        setupInputStream();
        when(socket.isConnected()).thenReturn(true);

        PipedInputStream pin = new PipedInputStream();
        PipedOutputStream pout = new PipedOutputStream(pin);

        // Der object input stream block im constructor daher muss dieser nach der Testklasse
        // instanziert werden da sonst keine Streamsignatur gesendet wird.
        when(socket.getOutputStream()).thenReturn(pout);
        connection = new ConcreteConnection(socket, handler);
        this.in = new ObjectInputStream(pin);

        this.outputPIn = pin;
        this.outputPOut = pout;

        //schliese unterliegende streams wie die richtige close methode.
        doAnswer((invocationOnMock) -> {
            inputPOut.close();
            inputPIn.close();
            outputPOut.close();
            outputPIn.close();
            return null;
        }).when(socket).close();
    }

    @After
    public void tearDown() throws Exception {

        //schliese alle streams
        out.close();
        in.close();
        inputPOut.close();
        inputPIn.close();
        outputPOut.close();
        outputPIn.close();
    }

    /**
     * Erstelle eine input pipline fuer alle tests.
     * @throws Exception Wenn etwas schief geht.
     */
    private void setupInputStream() throws Exception
    {

        PipedInputStream pin = new PipedInputStream();
        PipedOutputStream pout = new PipedOutputStream(pin);
        ObjectOutputStream outStream = new ObjectOutputStream(pout);
        when(socket.getInputStream()).thenReturn(pin);

        this.out = outStream;
        this.inputPIn = pin;
        this.inputPOut = pout;
    }

    @Test
    public void testShutdownWhichShouldSucceed() throws Exception {
        connection.shutdown();
        verify(socket, times(1)).close();
        verify(handler, times(1)).connectionClosed();
    }

    @Test
    public void testStartReadingWhichShouldSucceed() throws Exception
    {
        connection.startReading();
        out.writeObject(new LoginSuccessMessage());
        out.writeObject(new LoginSuccessMessage());
        out.writeObject(new LoginFailedMessage());
        out.writeObject(new LoginFailedMessage());
        out.writeObject(new LoginFailedMessage());

        //workaround
        Thread.sleep(3000);
        connection.shutdown();

        verify(handler, times(5)).messageReceived(any(Message.class));
    }

    @Test
    public void testStartReadingTwiceWhichShouldFail() throws Exception
    {
        connection.startReading();

        expectedException.expect(IllegalStateException.class);
        connection.startReading();
        connection.shutdown();
    }

    @Test
    public void testSynchronousReadWhileAsynchronousReadingIsRunningWhichShouldFail() throws Exception
    {
        connection.startReading();
        expectedException.expect(IllegalStateException.class);

        connection.read();
        connection.shutdown();
    }

    @Test
    public void testSynchronousReadWithEOFStream() throws Exception
    {
        inputPOut.close();
        expectedException.expect(InterruptedException.class);
        expectedException.expectMessage("Der unterliegende Stream wurde waerend des Leseprozesses geschlossen.");

        connection.read();
        connection.shutdown();
    }

    @Test
    public void testSendWhichShouldSucceed() throws Exception
    {
        LoginFailedMessage ms1 = new LoginFailedMessage();
        ms1.setReason(LoginFailedMessage.LoginFailedReason.ACCESS_DENIED);

        LoginFailedMessage ms2 = new LoginFailedMessage();
        ms2.setReason(LoginFailedMessage.LoginFailedReason.LOBBY_FULL);

        LoginFailedMessage ms3 = new LoginFailedMessage();
        ms3.setReason(LoginFailedMessage.LoginFailedReason.NAME_ALREADY_USED);

        connection.send(ms1);
        connection.send(ms2);
        connection.send(ms3);

        LoginFailedMessage result1 = (LoginFailedMessage) in.readObject();
        LoginFailedMessage result2 = (LoginFailedMessage) in.readObject();
        LoginFailedMessage result3 = (LoginFailedMessage) in.readObject();

        assertEquals(ms1, result1);
        assertEquals(ms2, result2);
        assertEquals(ms3, result3);

        verify(handler, times(3)).messageSend(any(Message.class));
        connection.shutdown();
    }

    @Test
    public void testSendWithNullMessageWhichShouldFail() throws Exception
    {
        expectedException.expect(NullPointerException.class);
        connection.send(null);
        verifyNoMoreInteractions(handler);
        connection.shutdown();
    }

    @Test
    public void testSendWhileOutputIsAlreadyClosed() throws Exception
    {
        connection.shutdown();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Die ausgehende Verbindung wurde bereits heruntergefahren.");

        connection.send(new LoginSuccessMessage());
        verifyNoMoreInteractions(handler);
        connection.shutdown();
    }

}