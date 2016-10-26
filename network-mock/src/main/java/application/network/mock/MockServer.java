package application.network.mock;

import application.network.protocol.Message;
import application.network.protocol.server.Server;

import java.io.IOException;
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
        }
        return instance;
    }

    @Override
    public void listen(int port) throws IOException
    {

    }

    @Override
    public void shutdown()
    {

    }

    @Override
    public void send(Message message, String clientId) throws IOException
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
    public void addClientConnectedHandler(Consumer<String> clientConnectedHandler)
    {

    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> disconnectHandler)
    {

    }
}
