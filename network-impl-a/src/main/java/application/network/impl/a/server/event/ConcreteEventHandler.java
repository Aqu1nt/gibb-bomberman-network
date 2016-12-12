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
            }catch( RuntimeException e ){
                logger.error( "One of the handlers thrown a RuntimeException." , e );
            }
        }
    }

    @Override
    public void fireClientDisconnected( String playerName ) {
        for( Consumer<String> handler : disconnectedHandlers ){
            try{
                handler.accept( playerName );
            }catch( RuntimeException e ){
                logger.error( "A 'clientDisconnectedHandler' thrown a RuntimeException." , e );
            }
        }
    }

    @Override
    public void addMessageReceivedObserver( BiConsumer<Message,String> messageHandler ) {
        messageHandlers.add( messageHandler );
    }

    @Override
    public void addClientDisconnectedObserver( Consumer<String> clientDisconnectedHandler ) {
        disconnectedHandlers.add( clientDisconnectedHandler );
    }

}
