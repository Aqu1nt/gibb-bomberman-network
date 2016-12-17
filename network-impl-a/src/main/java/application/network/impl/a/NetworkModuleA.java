package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.client.ClientFactory;


/**
 * Implementierung des Netzwerk Modules der Gruppe A!
 */
public class NetworkModuleA implements NetworkModule {

    @Override
    public Server createServer() {
        return null;
    }

    @Override
    public ServerProxy createClient() {
        ClientFactory clientFactory = new ClientFactory();
        return clientFactory.getInstance();
    }

}
