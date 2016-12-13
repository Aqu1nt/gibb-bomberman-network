package application.network.impl.a.server;


import application.network.api.Message;
import application.network.api.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.*;

import static application.network.impl.a.utils.StaticHelpers.callSilent;
import static application.network.impl.a.utils.StaticHelpers.suppress;

public class BServer implements Server, Runnable
{
    private final Object SOCKET_LOCK = new Object();

    /**
     * The thread which is accepting new clients
     */
    private Thread executingThread;

    /**
     * The server socket that is used to accept new clients
     */
    private ServerSocket socket;

    /**
     * Funktion welche eine neue connection erstellt
     */
    private BiFunction<Socket, BServer, BServerConnection> connectionFactory = null;

    public BServer()
    {
        this((socket, server) -> callSilent(() -> new BServerConnection(socket, server)));
    }

    public BServer(BiFunction<Socket, BServer, BServerConnection> connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void listen(int port) throws IOException, IllegalStateException
    {
        synchronized (SOCKET_LOCK)
        {
            if (socket != null)
            {
                throw new IllegalStateException("Server is already running");
            }
            executingThread = new Thread(this);
            socket = new ServerSocket(port);
            executingThread.start();
        }
    }

    @Override
    public void run()
    {
        while(socket != null && !socket.isClosed())
        {
            Socket clientSocket = suppress(() -> socket.accept());
            if (clientSocket != null)
            {
                connectionFactory.apply(clientSocket, this);
            }
        }
    }

    @Override
    public void shutdown()
    {
        synchronized (SOCKET_LOCK)
        {
            if (socket != null)
            {
                suppress(() -> { socket.close();return null; });
            }
            socket = null;
        }
    }

    @Override
    public void send(Message message, String clientId)
    {

    }

    @Override
    public void broadcast(Message message)
    {

    }

    @Override
    public void addMessageHandler(BiConsumer<Message, String> handler)
    {

    }

    @Override
    public void addClientConnectedHandler(Function<String, Boolean> handler)
    {

    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> handler)
    {

    }

}
