package application.network.impl.a;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static application.network.impl.a.utils.StaticHelpers.runSilent;
import static application.network.impl.a.utils.StaticHelpers.sleep;

public class TestWithServer
{
    public final int PORT = 54321;
    protected ObjectOutputStream serverOut;
    protected ObjectInputStream serverIn;
    protected Thread serverThread;
    protected ServerSocket serverSocket;
    protected Socket socket;

    @Before
    public void setup_server()
    {
        serverThread = new Thread(() -> {
            runSilent(() -> {
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                if (socket != null) {
                    serverOut = new ObjectOutputStream(socket.getOutputStream());
                    serverIn = new ObjectInputStream(socket.getInputStream());
                }
            });
        });
        serverThread.start();
        sleep(50);
    }

    @After
    public void kill_server() throws IOException
    {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
