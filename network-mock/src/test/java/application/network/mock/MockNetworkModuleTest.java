package application.network.mock;

import application.network.protocol.Network;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test to verify that {@link MockNetworkModule} is being discovered correctly
 */
public class MockNetworkModuleTest
{
    @Test
    public void testModuleRegistration()
    {
        assertThat(Network.createClient(), is(sameInstance(MockServerProxy.get())));
        assertThat(Network.createServer(), is(sameInstance(MockServer.get())));
    }
}
