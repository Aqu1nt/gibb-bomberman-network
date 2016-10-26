package application.network.api;

import application.network.api.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

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
        Network.moduleEvaluationStrategy = Network.DEFAULT_MODULE_EVALUATION_STRATEGY;
        module = mock(NetworkModule.class);
        serviceLoader = mock(ServiceLoader.class);
        doAnswer(i -> { i.getArgumentAt(0, Consumer.class).accept(module); return null; }).when(serviceLoader).forEach(any());
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
        doAnswer(i -> {
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
        doNothing().when(serviceLoader).forEach(any());
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
}
