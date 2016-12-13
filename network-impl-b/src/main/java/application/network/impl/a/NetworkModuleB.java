package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.BServerProxy;
import application.network.impl.a.server.BServer;

/**
 * Implementierung des Netzwerk Modules der Gruppe B!
 */
public class NetworkModuleB implements NetworkModule
{
    @Override
    public Server createServer()
    {
        return new BServer();
    }

    @Override
    public ServerProxy createClient()
    {
        return new BServerProxy();
    }
}
