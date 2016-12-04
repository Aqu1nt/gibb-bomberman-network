package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.oldstuff.server.client.ClientImpl;
import application.network.impl.a.oldstuff.server.ServerImpl;


/**
 * Implementierung des Netzwerk Modules der Gruppe A!
 */
public class NetworkModuleA implements NetworkModule {

    @Override
    public Server createServer() {
        return new ServerImpl();
    }

    @Override
    public ServerProxy createClient() {
        return new ClientImpl();
    }

}
