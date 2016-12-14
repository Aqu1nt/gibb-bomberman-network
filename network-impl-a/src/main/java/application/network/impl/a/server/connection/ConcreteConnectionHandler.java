package application.network.impl.a.server.connection;

import application.network.api.Message;
import application.network.impl.a.server.event.EventDispatcher;
import application.network.impl.a.server.security.SecurityContext;


public class ConcreteConnectionHandler implements ConnectionHandler {


// memory /////////////////////////////////////////////////////////////////////

    private final EventDispatcher eventDispatcher;


// constructors ///////////////////////////////////////////////////////////////

    public ConcreteConnectionHandler( EventDispatcher eventDispatcher ){
        this.eventDispatcher = eventDispatcher;
    }


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void connectionCreated( Connection connection ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void messageReceived( Message msg, Connection connection ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void messageSend( Message msg, Connection connection ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void exceptionThrown( Connection connection, Exception exception ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void connectionClosed( Connection connection ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void send( Message msg, String playerName ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void broadcast( Message msg ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void setSecurityContext( SecurityContext securityContext ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

}
