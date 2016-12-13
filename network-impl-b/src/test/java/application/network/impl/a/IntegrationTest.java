package application.network.impl.a;

import application.network.api.Message;
import application.network.api.Network;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.BServerProxy;
import application.network.impl.a.server.BServer;
import lombok.Value;
import org.junit.Test;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static application.network.impl.a.utils.StaticHelpers.sleep;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class IntegrationTest
{
    @Value
    static class HelloWorldMessage implements Message
    {
        private String message;
    }

    @Test
    public void do_integration_test() throws IOException, LobbyFullException, ClientIdInUseException
    {
        // Step 1 get the instances
        Server server = Network.getServer();
        ServerProxy client = Network.getClient();
        assertThat(server, is(instanceOf(BServer.class)));
        assertThat(client, is(instanceOf(BServerProxy.class)));

        // Step 2 register connect & disconnect handler on server side
        Function<String, Boolean> connectedHandler = mock(Function.class);
        when(connectedHandler.apply(any())).thenReturn(true);
        server.addClientConnectedHandler(connectedHandler);
        Consumer<String> disconnectHandler = mock(Consumer.class);
        server.addClientDisconnectedHandler(disconnectHandler);


        // Step 3 register message handlers
        Consumer<Message> clientMessageHandler = mock(Consumer.class);
        BiConsumer<Message, String> serverMessageHandler = mock(BiConsumer.class);
        client.addMessageHandler(clientMessageHandler);
        server.addMessageHandler(serverMessageHandler);

        // Step 4 start server
        server.listen(55443);

        // Step 5 connect client to server
        client.connect("integration-client", "localhost", 55443);
        verify(connectedHandler).apply("integration-client");

        // Step 6 send message from client to server
        Message clientMessage = new HelloWorldMessage("Hello Server");
        client.send(clientMessage);
        sleep(100);
        verify(serverMessageHandler).accept(eq(clientMessage), eq("integration-client"));

        // Step 6 send message from server to client
        Message serverMessage = new HelloWorldMessage("Hello Client");
        server.send(serverMessage, "integration-client");
        sleep(100);
        verify(clientMessageHandler).accept(serverMessage);

        // Step 7 broadcast message to all client
        Message broadcastMessage = new HelloWorldMessage("Hello All");
        server.broadcast(broadcastMessage);
        sleep(100);
        verify(clientMessageHandler).accept(broadcastMessage);

        // Step 8 shutdown the client
        client.disconnect();
        sleep(100);
        verify(disconnectHandler).accept("integration-client");

        // Step 9 shutdown the server
        server.shutdown();
    }
}
