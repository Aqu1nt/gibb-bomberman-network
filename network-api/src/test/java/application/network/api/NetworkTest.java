package application.network.api;

import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Test to ensure the {@link Network} facade is working as expected
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Network.class,
        ServiceLoader.class
})
public class NetworkTest
{
    private ServiceLoader<NetworkModule> serviceLoader;
    private NetworkModule module;

    @Before
    @SuppressWarnings("unchecked")
    public void setupEnvironment() throws Exception
    {
        Network.usedModule = null;
        Network.clientInstance = null;
        Network.serverInstance = null;
        Network.moduleEvaluationStrategy = Network.DEFAULT_MODULE_EVALUATION_STRATEGY;
        module = mock(NetworkModule.class);
        when(module.createServer()).then(i -> mock(Server.class));
        when(module.createClient()).then(i -> mock(ServerProxy.class));
        serviceLoader = PowerMockito.mock(ServiceLoader.class);
        PowerMockito.doAnswer(i -> { i.getArgumentAt(0, Consumer.class).accept(module); return null; }).when(serviceLoader).forEach(any());
        mockStatic(ServiceLoader.class);
        when(ServiceLoader.load(eq(NetworkModule.class))).thenReturn(serviceLoader);
    }

    @Test
    public void testAutoDetection()
    {
        Network.createServer();
        verify(module).createServer();

        Network.createClient();
        verify(module).createClient();
    }

    @Test
    public void testNoAutoDetectionWhenModuleProvided()
    {
        Network.setNetworkModule(module);
        Network.createServer();
        Network.createClient();
        verifyStatic(never());
        ServiceLoader.load(any());
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNetworkModuleAlreadySet()
    {
        Network.setNetworkModule(module);
        Network.setNetworkModule(module);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testExceptionWhenMultipleNetworkModulesAvailable()
    {
        PowerMockito.doAnswer(i -> {
            Consumer<NetworkModule> consumer = i.getArgumentAt(0, Consumer.class);
            consumer.accept(module);
            consumer.accept(module);
            return null;
        }).when(serviceLoader).forEach(any());

        Network.createServer();
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNoNetworkModuleAvailable()
    {
        PowerMockito.doNothing().when(serviceLoader).forEach(any());
        Network.createServer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullpointerWhenNullNetworkModule()
    {
        Network.setNetworkModule(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCustomModuleEvaluationStrategy()
    {
        NetworkModule mockModule = mock(NetworkModule.class);
        Server mockServer = mock(Server.class);
        when(mockModule.createServer()).thenReturn(mockServer);
        Function<List<NetworkModule>, NetworkModule> strategy = list -> mockModule;
        Network.setModuleEvaluationStrategy(strategy);
        assertThat(Network.createServer(), is(sameInstance(mockServer)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCustomEvaluationStrategy()
    {
        Network.setModuleEvaluationStrategy(null);
    }

    @Test
    public void testSingletonMethodsCreateOnlyOneObject()
    {
        Server server1 = Network.getServer();
        Server server2 = Network.getServer();
        ServerProxy client1 = Network.getClient();
        ServerProxy client2 = Network.getClient();
        verify(module).createServer();
        verify(module).createClient();
        assertThat(server1, is(sameInstance(server2)));
        assertThat(client1, is(sameInstance(client2)));
    }

    @Test
    public void testFactoryMethodsCreateMultipleObjects()
    {
        Server server1 = Network.createServer();
        Server server2 = Network.createServer();
        ServerProxy client1 = Network.createClient();
        ServerProxy client2 = Network.createClient();
        verify(module, times(2)).createServer();
        verify(module, times(2)).createClient();
        assertThat(server1, is(not(sameInstance(server2))));
        assertThat(client1, is(not(sameInstance(client2))));
    }

    static class MockServerImpl implements Server {
        public BiConsumer<Message, String> handler;
        public void listen(int port) throws IOException, IllegalStateException {}
        public void shutdown() {}
        public void send(Message message, String clientId) {}
        public void broadcast(Message message) {}
        public void addMessageHandler(BiConsumer<Message, String> handler) { this.handler = handler; }
        public void addClientConnectedHandler(Function<String, Boolean> handler) {}
        public void addClientDisconnectedHandler(Consumer<String> handler) {}
    }

    class Message1 implements Message {}
    class Message2 implements Message {}

    @Test
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void testSpecialServerMessageHandlers()
    {
        MockServerImpl server = spy(new MockServerImpl());

        //Test filter
        BiConsumer<Message1, String> h = (msg, id) -> assertThat(msg, is(instanceOf(Message1.class)));
        server.addMessageHandler(Message1.class, h);
        server.handler.accept(new Message1(), "");
        server.handler.accept(new Message2(), "");

        //Test id mapper
        Map<String, Consumer<Message>> consumerMap = new HashMap<>();
        Consumer<Message> consumer1 = mock(Consumer.class);
        Consumer<Message> consumer2 = mock(Consumer.class);
        consumerMap.put("client1", consumer1);
        consumerMap.put("client2", consumer2);
        server.addDispatchingMessageHandler(consumerMap::get);
        server.handler.accept(mock(Message.class), "client1");
        verify(consumer1).accept(any());
        verify(consumer2, never()).accept(any());
        Mockito.reset(consumer1, consumer2);
        server.handler.accept(mock(Message.class), "client2");
        verify(consumer2).accept(any());
        verify(consumer1, never()).accept(any());
    }

    class MockServerProxyImpl implements ServerProxy
    {
        private Consumer<Message> handler = null;
        public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException {}
        public void disconnect() {}
        public void send(Message message) {}
        public void addMessageHandler(Consumer<Message> handler) { this.handler = handler; }
        public void addServerDisconnectedHandler(Runnable handler) {}
    }

    @Test
    public void testSpecialServerProxyHandler()
    {
        MockServerProxyImpl client = spy(new MockServerProxyImpl());

        Consumer<Message1> h = (msg) -> assertThat(msg, is(instanceOf(Message1.class)));
        client.addMessageHandler(Message1.class, h);
        client.handler.accept(new Message1());
        client.handler.accept(new Message2());
    }
}
