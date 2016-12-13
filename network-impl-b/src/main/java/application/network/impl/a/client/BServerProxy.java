package application.network.impl.a.client;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;

import java.io.IOException;
import java.util.function.Consumer;

public class BServerProxy implements ServerProxy
{
    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException
    {

    }

    @Override
    public void disconnect()
    {

    }

    @Override
    public void send(Message message)
    {

    }

    @Override
    public void addMessageHandler(Consumer<Message> handler)
    {

    }

    @Override
    public void addServerDisconnectedHandler(Runnable handler)
    {

    }
}
