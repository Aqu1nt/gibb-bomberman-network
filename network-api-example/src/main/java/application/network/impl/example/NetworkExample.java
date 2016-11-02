package application.network.impl.example;

import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;

import java.io.IOException;

/**
 * Demo Klasse
 */
public class NetworkExample
{

    public void startupServer() throws IOException
    {
    }

    public void startupClient() throws LobbyFullException, IOException, ClientIdInUseException
    {

    }

    public static void main(String[] args) throws IOException, LobbyFullException, ClientIdInUseException
    {
        NetworkExample example = new NetworkExample();
        example.startupServer();
        example.startupClient();
    }
}
