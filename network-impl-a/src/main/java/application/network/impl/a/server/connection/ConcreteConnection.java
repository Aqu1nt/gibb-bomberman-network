package application.network.impl.a.server.connection;

import application.network.api.Message;

import java.net.Socket;
import java.util.UUID;


public class ConcreteConnection implements Connection {


// memory /////////////////////////////////////////////////////////////////////

    private final Socket socket;
    private final ConnectionHandler connectionHandler;


// constructors ///////////////////////////////////////////////////////////////

    public ConcreteConnection( Socket socket , ConnectionHandler connectionHandler ){
        this.socket = socket;
        this.connectionHandler = connectionHandler;
    }


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void send( Message msg ) {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public UUID getId() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public String getPublicId() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void setPublicId() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not implemented yet"); // TODO: implement this method.
    }

}
