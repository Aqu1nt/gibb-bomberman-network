package application.network.implA;

import application.network.api.Network;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

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
        Network.getServer();
        Network.getClient();
        assertThat(Whitebox.getInternalState(Network.class, "usedModule"), is(instanceOf(NetworkModuleA.class)));
    }
}