package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;


/**
 * Implementierung des Netzwerk Modules der Gruppe A!
 */
public class NetworkModuleA implements NetworkModule {

    @Override
    public Server createServer() {
        throw new UnsupportedOperationException( "Implementation not configured yet." );
    }

    @Override
    public ServerProxy createClient() {
        throw new UnsupportedOperationException( "Implementation not configured yet." );
    }

}
