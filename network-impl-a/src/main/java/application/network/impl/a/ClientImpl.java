package application.network.impl.a;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;

import java.io.IOException;
import java.util.function.Consumer;


public class ClientImpl implements ServerProxy {


// memory /////////////////////////////////////////////////////////////////////

    //private Socket socket;


// constructor ////////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void connect( String clientId , String ip , int port ) throws IOException, ClientIdInUseException, LobbyFullException {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    @Override
    public void send( Message message ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    @Override
    public void addMessageHandler( Consumer<Message> handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    @Override
    public void addServerDisconnectedHandler( Runnable handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

}
