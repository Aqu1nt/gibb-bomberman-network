package application.network.mock;

import application.network.api.Message;
import application.network.api.server.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        get().messageHandlers.forEach(m -> m.accept(message, clientId));
    }

    public static void simulateClientConnect(String clientId)
    {
        get().clientConnectedHandlers.forEach(c -> c.accept(clientId));
    }


    public static void simulateClientDisconnect(String clientId)
    {
        get().clientDisconnectedHandlers.forEach(c -> c.accept(clientId));
    }

    public static void reset()
    {
        get().open = false;
        get().messageHandlers = new ArrayList<>();
        get().clientConnectedHandlers = new ArrayList<>();
        get().clientDisconnectedHandlers = new ArrayList<>();
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
        Objects.requireNonNull(message);
        Objects.requireNonNull(clientId);
        if (!open) {
            throw new IOException("MockServer is not running yet, see the listen() method");
        }
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(message);
    }

    @Override
    public void broadcast(Message message) throws IOException
    {
        if (!open) {
            throw new IOException("MockServer is not running yet, see the listen() method");
        }
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(message);
    }

    @Override
    public void addMessageHandler(BiConsumer<Message, String> handler)
    {
        messageHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addClientConnectedHandler(Consumer<String> clientConnectedHandler)
    {
        clientConnectedHandlers.add(Objects.requireNonNull(clientConnectedHandler));
    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> disconnectHandler)
    {
        clientDisconnectedHandlers.add(Objects.requireNonNull(disconnectHandler));
    }
}
