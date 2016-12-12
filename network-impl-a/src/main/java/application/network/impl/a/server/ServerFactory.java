package application.network.impl.a.server;

import application.network.api.server.Server;
import application.network.impl.a.server.event.ConcreteEventHandler;
import application.network.impl.a.server.event.EventManager;


public class ServerFactory {


// memory /////////////////////////////////////////////////////////////////////


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    /**
     * TODO: Specify if this method handles a singleton or creates new instances each time.
     */
    public Server getServerInstance(){
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public EventManager createEventManager(){
        return new ConcreteEventHandler();
    }

//    public ConnectionFactory createConnectionFactory(){
//        throw new UnsupportedOperationException( "Not implemented yet" );
//    }

//    public ConnectionHandler createConnectionHandler(){
//        throw new UnsupportedOperationException( "Not implemented yet" );
//    }

//    public SecurityManager createSecurityManager(){
//        throw new UnsupportedOperationException( "Not implemented yet" );
//    }

}
