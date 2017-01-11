package application.network.impl.a.server.connection;

import application.network.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * Represents a connection to a client.
 */
public class ClientHandle implements Connection, Closeable {


// memory /////////////////////////////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger( ClientHandle.class );
    private final Object listenMutex = new Object();
    private final ArrayList<Consumer<Message>> messageHandlers = new ArrayList<>();
    private final ArrayList<Runnable> disconnectedHandlers = new ArrayList<>();
    private String playerName;
    private Socket socket;
    private ObjectOutputStream objOStream;
    private ObjectInputStream objIStream;


// constructor ////////////////////////////////////////////////////////////////

    ClientHandle( Socket socket ){
        this.socket = socket;

        // Setup the output stream for this client.
        try{
            objOStream = new ObjectOutputStream( socket.getOutputStream() );
            objOStream.flush();
        }catch( IOException e ){
            logger.error( "Failed to initialize object output stream." , e );
            return;
        }

        // Setup the input stream for this client.
        try{
            objIStream = new ObjectInputStream(socket.getInputStream());
        }catch( IOException e ){
            logger.error( "Failed to initialize object input stream." , e );
        }

    }


// methods ////////////////////////////////////////////////////////////////////

    /**
     * Does listen and fires the messages as they get received.
     * This method is blocking.
     */
    public void listen(){
        synchronized( listenMutex ){
            while( true ){ // <-- TODO: Break currently is achieved with exception handing. Do explicitly handle break.
                logger.debug( "Blocking until next message arrives ..." );
                Object obj;
                try{
                    obj = objIStream.readObject();
                }catch( IOException e ){
                    // Client did close its output. Fire event.
                    try{
                        for( Runnable disconnectHandler : disconnectedHandlers ){
                            disconnectHandler.run();
                        }
                    }finally{
                        close();
                    }
                    break;
                }catch( ClassNotFoundException e ){
                    throw new RuntimeException( "Failed to deserialize object." , e ); // TODO: handle
                }
                logger.trace( "Object of type '"+obj.getClass().getName()+"' received." );
                if(!( obj instanceof Message )){
                    throw new RuntimeException("Received object is of invalid type. Super type "+Message.class.getName()+" missing.\n\tExpected: "+Message.class.getName()+"\n\t  Actual: "+obj.getClass().getName());
                }
                Message msg = (Message)obj;
                for( Consumer<Message> handler : messageHandlers ){
                    handler.accept( msg );
                }
            }
        }
    }

    public void send( Message message ) {
        try{
            objOStream.writeObject( message );
        }catch( IOException e ){
            logger.error( "Failed to send message." , e );
        }
    }

    public void addMessageHandler( Consumer<Message> handler ) {
        synchronized( messageHandlers ){
            messageHandlers.add( handler );
        }
    }

    public void removeMessageHandler( Consumer<Message> handler ){
        synchronized( messageHandlers ){
            messageHandlers.remove( handler );
        }
    }

    public void addDisconnectedHandler( Runnable handler ) {
        synchronized( disconnectedHandlers ){
            disconnectedHandlers.add( handler );
        }
    }

    public void setPlayerName( String playerName ) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void close() {
        try{
            synchronized( this ){
                if( socket != null ) socket.close();
            }
        }catch( IOException e ){
            logger.error( "Failed to close socket." , e );
        }
    }

    public boolean equalsInName( ClientHandle other ){
        return
            // Other is equal with me if other exists ...
            other != null
                &&  // ... AND ...
                (
                    // ... Both names are null ...
                    getPlayerName() == null && other.getPlayerName() == null
                        // ... OR I have a name and it equals to the others name.
                        || getPlayerName() != null && getPlayerName().equals( other.getPlayerName() )
                )
        ;
    }

}
