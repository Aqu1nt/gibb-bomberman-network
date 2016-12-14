package application.network.impl.a.server.connection;

import java.net.Socket;


public class ConcreteConnectionFactory implements ConnectionFactory {


// memory /////////////////////////////////////////////////////////////////////

    private final ConnectionHandler connectionHandler;


// constructors ///////////////////////////////////////////////////////////////

    public ConcreteConnectionFactory( ConnectionHandler connectionHandler ){
        this.connectionHandler = connectionHandler;
    }


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public Connection create( Socket socket ) {
        return new ConcreteConnection( socket , connectionHandler );
    }


}
