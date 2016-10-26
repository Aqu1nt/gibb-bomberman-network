package application.network.mock;

import application.network.protocol.Message;
import application.network.protocol.client.ClientIdInUseException;
import application.network.protocol.client.LobbyFullException;
import application.network.protocol.client.ServerProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MockServerProxy implements ServerProxy
{

    private static MockServerProxy instance;

    public synchronized static MockServerProxy get()
    {
        if (instance == null)
        {
            instance = new MockServerProxy();
        }
        return instance;
    }

    public static void simulateServerDisconnect()
    {
        instance.serverDisconnectedHandlers.forEach(Runnable::run);
    }

    public static void simulateMessage(Message message)
    {
        instance.messageHandlers.forEach(h -> h.accept(message));
    }

    public static void reset()
    {
        instance.connected = false;
        instance.messageHandlers = new ArrayList<>();
        instance.serverDisconnectedHandlers = new ArrayList<>();
    }

    private boolean connected;
    private List<Consumer<Message>> messageHandlers;
    private List<Runnable> serverDisconnectedHandlers;

    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException
    {
        if (connected) {
            throw new IOException("MockServerProxy is already connected!");
        }
        connected = true;
    }

    @Override
    public void disconnect()
    {
        connected = false;
    }

    @Override
    public void send(Message message) throws IOException
    {
        if (!connected) {
            throw new IOException("MockServerProxy is not connected yet, see the connect() method");
        }
    }

    @Override
    public void addMessageHandler(Consumer<Message> handler)
    {
        messageHandlers.add(handler);
    }

    @Override
    public void addServerDisconnectedHandler(Runnable serverDisconnectedHandler)
    {
        serverDisconnectedHandlers.add(serverDisconnectedHandler);
    }
}
