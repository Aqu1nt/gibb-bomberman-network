package application.network.mock;

import application.network.protocol.NetworkModule;
import application.network.protocol.client.ServerProxy;
import application.network.protocol.server.Server;

/**
 * Network modul als Hilfe für die Implementierung
 */
public class MockNetworkModule implements NetworkModule
{
    @Override
    public Server createServer()
    {
        return MockServer.get();
    }

    @Override
    public ServerProxy createClient()
    {
        return MockServerProxy.get();
    }
}
