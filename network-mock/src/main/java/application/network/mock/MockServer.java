package application.network.mock;

import application.network.protocol.Message;
import application.network.protocol.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MockServer implements Server
{
    private static MockServer instance;

    public synchronized static MockServer get()
    {
        if (instance == null)
        {
            instance = new MockServer();
            reset();
        }
        return instance;
    }

    public static void simulateMessage(Message message, String clientId)
    {
        instance.messageHandlers.forEach(m -> m.accept(message, clientId));
    }

    public static void simulateClientConnect(String clientId)
    {
        instance.clientConnectedHandlers.forEach(c -> c.accept(clientId));
    }


    public static void simulateClientDisconnect(String clientId)
    {
        instance.clientDisconnectedHandlers.forEach(c -> c.accept(clientId));
    }

    public static void reset()
    {
        instance.open = false;
        instance.messageHandlers = new ArrayList<>();
        instance.clientConnectedHandlers = new ArrayList<>();
        instance.clientDisconnectedHandlers = new ArrayList<>();
    }

    private boolean open;
    private List<BiConsumer<Message, String>> messageHandlers;
    private List<Consumer<String>> clientConnectedHandlers;
    private List<Consumer<String>> clientDisconnectedHandlers;

    @Override
    public void listen(int port) throws IOException
    {
        if (open) {
            throw new IllegalStateException("Server already running");
        }
        open = true;
    }

    @Override
    public void shutdown()
    {
        open = false;
    }

    @Override
    public void send(Message message, String clientId) throws IOException
    {
        if (!open) {
            throw new IOException("MockServer is not running yet, see the listen() method");
        }
    }

    @Override
    public void broadcast(Message message) throws IOException
    {
        if (!open) {
            throw new IOException("MockServer is not running yet, see the listen() method");
        }
    }

    @Override
    public void addMessageHandler(BiConsumer<Message, String> handler)
    {
        messageHandlers.add(handler);
    }

    @Override
    public void addClientConnectedHandler(Consumer<String> clientConnectedHandler)
    {
        clientConnectedHandlers.add(clientConnectedHandler);
    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> disconnectHandler)
    {
        clientDisconnectedHandlers.add(disconnectHandler);
    }
}
