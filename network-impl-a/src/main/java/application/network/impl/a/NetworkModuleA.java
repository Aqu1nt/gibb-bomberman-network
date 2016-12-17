package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
import application.network.impl.a.server.ServerFactory;
import application.network.impl.a.client.ClientFactory;


/**
 * Implementierung des Netzwerk Modules der Gruppe A!
 */
public class NetworkModuleA implements NetworkModule {


// memory /////////////////////////////////////////////////////////////////////

    private ServerFactory serverFactory;


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public Server createServer() {
        return getServerFactory().getServer();
    }

    @Override
    public ServerProxy createClient() {
        ClientFactory clientFactory = new ClientFactory();
        return clientFactory.getInstance();
    }

    private ServerFactory getServerFactory(){
        synchronized( this ){
            if( serverFactory == null ) serverFactory = new ServerFactory();
        }
        return serverFactory;
    }

}
