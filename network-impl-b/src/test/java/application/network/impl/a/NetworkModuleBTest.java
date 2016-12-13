package application.network.impl.a;

import application.network.api.Network;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.BServerProxy;
import application.network.impl.a.server.BServer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test to verify {@link NetworkModuleB}
 */
public class NetworkModuleBTest
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