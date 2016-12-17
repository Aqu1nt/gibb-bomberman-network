package application.network.impl.a;

import application.network.api.NetworkModule;
import application.network.api.client.ServerProxy;
import application.network.api.server.Server;
<<<<<<< HEAD
import application.network.impl.a.server.ServerFactory;
=======
import application.network.impl.a.client.ClientFactory;
>>>>>>> feature/client_implementation_team_a


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
<<<<<<< HEAD
        return getServerFactory().getServer();
=======
        return null;
>>>>>>> feature/client_implementation_team_a
    }

    @Override
    public ServerProxy createClient() {
<<<<<<< HEAD
        throw new UnsupportedOperationException( "Implementation not configured yet." );
    }

    private ServerFactory getServerFactory(){
        synchronized( this ){
            if( serverFactory == null ) serverFactory = new ServerFactory();
        }
        return serverFactory;
=======
        ClientFactory clientFactory = new ClientFactory();
        return clientFactory.getInstance();
>>>>>>> feature/client_implementation_team_a
    }

}
