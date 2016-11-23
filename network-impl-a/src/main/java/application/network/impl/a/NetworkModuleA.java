package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.AServerProxy;
import application.network.impl.a.server.AServer;

/**
 * Implementierung des Netzwerk Modules der Gruppe A!
 */
public class NetworkModuleA implements NetworkModule
{
    @Override
    public Server createServer()
    {
        return new AServer();
    }

    @Override
    public ServerProxy createClient()
    {
        return new AServerProxy();
    }
}
