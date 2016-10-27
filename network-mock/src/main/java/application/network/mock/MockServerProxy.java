package application.network.mock;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        get().serverDisconnectedHandlers.forEach(Runnable::run);
        get().connected = false;
    }

    public static void simulateMessage(Message message)
    {
        get().messageHandlers.forEach(h -> h.accept(message));
    }

    public static void simulateLobbyFull()
    {
        get().simulateLobbyFull = true;
    }

    public static void simulateLobbyEmpty()
    {
        get().simulateLobbyFull = false;
    }

    public static void simulateClientIdInUse()
    {
        get().simulateClientIdInUse = true;
    }

    public static void simulateClientIdFree()
    {
        get().simulateClientIdInUse = false;
    }

    public static void reset()
    {
        get().connected = false;
        get().messageHandlers = new ArrayList<>();
        get().serverDisconnectedHandlers = new ArrayList<>();
        get().simulateClientIdInUse = false;
        get().simulateLobbyFull = false;
    }

    private boolean connected;
    private List<Consumer<Message>> messageHandlers;
    private List<Runnable> serverDisconnectedHandlers;
    private boolean simulateLobbyFull;
    private boolean simulateClientIdInUse;

    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException
    {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(ip);
        if (connected) {
            throw new IOException("MockServerProxy is already connected!");
        }
        if (simulateLobbyFull) {
            throw new LobbyFullException();
        }
        if (simulateClientIdInUse) {
            throw new ClientIdInUseException();
        }
        connected = true;
    }

    @Override
    public void disconnect()
    {
        connected = false;
    }

    @Override
    public void send(Message message)
    {
        try
        {
            Objects.requireNonNull(message);
            if (!connected)
            {
                throw new IOException("MockServerProxy is not connected yet, see the connect() method");
            }
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(message);
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void addMessageHandler(Consumer<Message> handler)
    {
        messageHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addServerDisconnectedHandler(Runnable serverDisconnectedHandler)
    {
        serverDisconnectedHandlers.add(Objects.requireNonNull(serverDisconnectedHandler));
    }
}
