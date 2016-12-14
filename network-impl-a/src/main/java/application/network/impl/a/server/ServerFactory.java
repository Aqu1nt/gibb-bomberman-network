package application.network.impl.a.server;

import application.network.api.server.Server;
import application.network.impl.a.server.connection.ConcreteConnectionFactory;
import application.network.impl.a.server.connection.ConcreteConnectionHandler;
import application.network.impl.a.server.connection.ConnectionFactory;
import application.network.impl.a.server.connection.ConnectionHandler;
import application.network.impl.a.server.event.ConcreteEventHandler;
import application.network.impl.a.server.event.EventManager;
import application.network.impl.a.server.security.ConcreteSecurityManager;
import application.network.impl.a.server.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerFactory {


// memory /////////////////////////////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger( ServerFactory.class );
    private ConnectionFactory connectionFactory;
    private ConnectionHandler connectionHandler;
    private EventManager eventManager;
    private ExecutorService executorService;
    private SecurityManager securityManager;
    private Server server;


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    public Server getServer(){
        synchronized( this ){
            if( server == null ) server = new ConcreteServer( getSecurityManager() , getConnectionHandler() , getEventManager() , getConnectionFactory() , getExecutorService() );
        }
        return server;
    }

    public ExecutorService getExecutorService(){
        synchronized( this ){
            if( executorService == null ) executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    public EventManager getEventManager(){
        synchronized( this ){
            if( eventManager == null ) eventManager = new ConcreteEventHandler();
        }
        return eventManager;
    }

    public ConnectionFactory getConnectionFactory(){
        synchronized( this ){
            if( connectionFactory == null ) connectionFactory = new ConcreteConnectionFactory( getConnectionHandler() );
        }
        return connectionFactory;
    }

    public ConnectionHandler getConnectionHandler(){
        synchronized( this ){
            if( connectionHandler == null ) connectionHandler = new ConcreteConnectionHandler( getEventManager() );
        }
        return connectionHandler;
    }

    public SecurityManager getSecurityManager(){
        synchronized( this ){
            if( securityManager == null ) securityManager = new ConcreteSecurityManager();
        }
        return securityManager;
    }

}
