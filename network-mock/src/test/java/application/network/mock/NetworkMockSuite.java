package application.network.mock;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    MockNetworkModuleTest.class,
    MockServerProxyTest.class,
    MockServerTest.class
})
public class NetworkMockSuite
{
}
