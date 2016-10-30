package application.network.mock;

import application.network.api.Message;
import application.network.api.server.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.spy;

public class MockServer implements Server
{
    //Replace this in order to use your custom server implementation like a
    //Mockito mock
    public static Supplier<MockServer> serverFactory = () -> spy(new MockServer());

    //The singleton instance
    private static MockServer instance;

    public synchronized static MockServer get()
    {
        if (instance == null)
        {
            instance = serverFactory.get();
            reset();
        }
        return instance;
    }

    public static void simulateMessage(Message message, String clientId)
    {
        get().messageHandlers.forEach(m -> m.accept(message, clientId));
    }

    public static boolean simulateClientConnect(String clientId)
    {
        return !get().clientConnectedHandlers.stream()
                .map(c -> c.apply(clientId))
                .anyMatch(c -> !c) || get().clientConnectedHandlers.isEmpty();
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
    private List<Function<String, Boolean>> clientConnectedHandlers;
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
    public void send(Message message, String clientId)
    {
        try
        {
            Objects.requireNonNull(message);
            Objects.requireNonNull(clientId);
            if (!open)
            {
                throw new IOException("MockServer is not running yet, see the listen() method");
            }
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(message);
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void broadcast(Message message)
    {
        try
        {
            if (!open) {
                throw new IOException("MockServer is not running yet, see the listen() method");
            }
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(message);
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void addMessageHandler(BiConsumer<Message, String> handler)
    {
        messageHandlers.add(Objects.requireNonNull(handler));
    }

    @Override
    public void addClientConnectedHandler(Function<String, Boolean> clientConnectedHandler)
    {
        clientConnectedHandlers.add(Objects.requireNonNull(clientConnectedHandler));
    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> disconnectHandler)
    {
        clientDisconnectedHandlers.add(Objects.requireNonNull(disconnectHandler));
    }
}
