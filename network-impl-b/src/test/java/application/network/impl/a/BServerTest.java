package application.network.impl.a;

import application.network.impl.a.server.BServer;
import application.network.impl.a.server.BServerConnection;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.Socket;
import java.util.function.BiFunction;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
public class BServerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BServer server;

    @After
    public void shutdownServerAfterTest()
    {
        if (server != null) {
            server.shutdown();
        }
    }

    @Test(timeout = 1000)
    public void serverListenShouldOpenPortAndNotBlock() throws IOException
    {
        server = new BServer();
        server.listen(50000);

        new Socket("localhost", 50000);
        new Socket("localhost", 50000);
        new Socket("localhost", 50000);
    }

    @Test
    public void serverShouldBeStartableWhenRunning() throws IOException
    {
        server = new BServer();
        server.listen(50000);
        thrown.expect(IllegalStateException.class);
        server.listen(50000);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serverShouldCreateNewServerConnections() throws IOException
    {
        BiFunction<Socket, BServer, BServerConnection> mockFactory = mock(BiFunction.class);
        server = new BServer(mockFactory);
        server.listen(50000);

        new Socket("localhost", 50000);
        verify(mockFactory).apply(any(), eq(server));
    }
}