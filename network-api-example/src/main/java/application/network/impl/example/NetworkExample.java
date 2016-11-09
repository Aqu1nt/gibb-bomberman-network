package application.network.impl.example;

import application.network.api.Network;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.mock.MockServer;
import application.network.mock.MockServerProxy;

import java.io.IOException;

/**
 * Demo Klasse
 */
public class NetworkExample
{

    public void startupServer() throws IOException
    {
        Server server = Network.getServer();
        server.listen(1000);
        server.addMessageHandler((msg, clientId) -> {
            System.out.println("Server received message "+msg+" from "+clientId);
        });
        //Your application

        MockServer.simulateMessage(new ExampleMessage(), "test-client");

        server.shutdown();
    }

    public void startupClient() throws LobbyFullException, IOException, ClientIdInUseException
    {
        ServerProxy client = Network.getClient();
        client.connect("test-client", "localhost", 1000);
        client.addMessageHandler(msg -> {
            System.out.println("Client received message "+msg);
        });

        MockServerProxy.simulateMessage(new ExampleMessage());

        client.disconnect();
    }

    public static void main(String[] args) throws IOException, LobbyFullException, ClientIdInUseException
    {
        NetworkExample example = new NetworkExample();
        example.startupServer();
        example.startupClient();
    }
}
