package application.network.impl.a;

import application.network.api.Network;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.AServerProxy;
import application.network.impl.a.server.AServer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test to verify {@link NetworkModuleA}
 */
public class NetworkModuleATest
{
    @Test
    public void testNetworkModuleARegistrationOnNetworkFactory()
    {
        Server server = Network.getServer();
        ServerProxy client = Network.getClient();
        assertThat(server, is(instanceOf(AServer.class)));
        assertThat(client, is(instanceOf(AServerProxy.class)));
    }
}