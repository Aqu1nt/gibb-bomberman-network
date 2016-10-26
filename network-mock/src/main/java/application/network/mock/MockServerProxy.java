package application.network.mock;

import application.network.protocol.Message;
import application.network.protocol.client.ClientIdInUseException;
import application.network.protocol.client.LobbyFullException;
import application.network.protocol.client.ServerProxy;

import java.io.IOException;
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

    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException
    {

    }

    @Override
    public void disconnect()
    {

    }

    @Override
    public void send(Message message) throws IOException
    {

    }

    @Override
    public void addMessageHandler(Consumer<Message> handler)
    {

    }

    @Override
    public void addServerDisconnectedHandler(Runnable serverDisconnectedHandler)
    {

    }
}
