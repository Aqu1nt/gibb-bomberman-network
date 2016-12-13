package application.network.impl.a;

import application.network.api.Network;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.BServerProxy;
import application.network.impl.a.client.BServerProxyTest;
import application.network.impl.a.server.BServer;
import application.network.impl.a.server.BServerConnectionTest;
import application.network.impl.a.server.BServerTest;
import application.network.impl.a.utils.NetworkConnectionTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test to verify {@link NetworkModuleB}
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    BServerTest.class,
    NetworkConnectionTest.class,
    BServerConnectionTest.class,
    BServerProxyTest.class
})
public class NetworkModuleBTests
{
    @Test
    public void testNetworkModuleARegistrationOnNetworkFactory()
    {
        Server server = Network.getServer();
        ServerProxy client = Network.getClient();
        assertThat(server, is(instanceOf(BServer.class)));
        assertThat(client, is(instanceOf(BServerProxy.class)));
    }
}