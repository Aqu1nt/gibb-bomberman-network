package application.network.mock;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;

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
