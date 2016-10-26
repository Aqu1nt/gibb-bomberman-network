package application.network.mock;

import org.junit.Before;
import org.junit.Test;

/**
 * Test to verify {@link MockServer} is working as expected
 */
public class MockServerTest
{
    @Before
    public void resetMocks()
    {
        MockServer.reset();
    }

    @Test
    public void testStartServer()
    {

    }
}
