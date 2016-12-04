package application.network.impl.a.oldstuff.server;

import application.network.api.Message;
import application.network.api.server.Server;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


public class ServerImpl implements Server {


// memory /////////////////////////////////////////////////////////////////////


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void listen( int port ) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void send( Message message, String clientId ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void broadcast( Message message ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addMessageHandler( BiConsumer<Message,String> handler ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addClientConnectedHandler( Function<String,Boolean> handler ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addClientDisconnectedHandler( Consumer<String> handler ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
