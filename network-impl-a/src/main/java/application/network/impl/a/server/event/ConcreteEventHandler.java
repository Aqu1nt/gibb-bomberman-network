package application.network.impl.a.server.event;

import application.network.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class ConcreteEventHandler implements EventManager {


// memory /////////////////////////////////////////////////////////////////////

    private final Logger logger = LoggerFactory.getLogger( this.getClass() );
    private final ConcurrentSkipListSet<BiConsumer<Message,String>> messageHandlers = new ConcurrentSkipListSet<>();
    private final ConcurrentSkipListSet<Consumer<String>> disconnectedHandlers = new ConcurrentSkipListSet<>();


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void fireMessageReceived( Message msg, String playerName ) {
        for( BiConsumer<Message,String> messageHandler : messageHandlers ){
            try{
                messageHandler.accept( msg , playerName );
            }catch( Exception e ){
                logger.error( "One of the handlers throwed an exception." , e );
            }
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void fireClientDisconnected( String playerName ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addMessageReceivedObserver( BiConsumer<Message,String> messageHandler ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addClientDisconnectedObserver( Consumer<String> clientDisconnectedHandler ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
