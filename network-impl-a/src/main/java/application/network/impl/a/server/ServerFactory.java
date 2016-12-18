package application.network.impl.a.server;

import application.network.api.server.Server;
import application.network.impl.a.server.connection.ConcreteConnectionFactory;
import application.network.impl.a.server.connection.ConnectionFactory;


public class ServerFactory {


// memory /////////////////////////////////////////////////////////////////////

    private ConnectionFactory connectionFactory;
    private Server server;


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    public Server getServer(){
        synchronized( this ){
            if( server == null ) server = new ConcreteServer( getConnectionFactory() );
        }
        return server;
    }

    ConnectionFactory getConnectionFactory(){
        synchronized( this ){
            if( connectionFactory == null ) connectionFactory = new ConcreteConnectionFactory();
        }
        return connectionFactory;
    }

}
