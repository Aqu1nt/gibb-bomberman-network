package application.network.impl.a.oldstuff.server;

import application.network.api.Message;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;


/**
 * Mit diesem handle kann der server einen client verwalten. Eine instanz dieses Types kann nur für genau eine Verbindung
 * verwendet werden. Der socket ist immutable und kann sobald einmal gesetzt nicht geändert werden.
 */
public class ClientHandle implements Closeable {


// memory /////////////////////////////////////////////////////////////////////

    private final Socket socket;
    private String playerName;


// constructors ///////////////////////////////////////////////////////////////

    public ClientHandle( Socket socket ){
        this.socket = socket;
        // TODO: Initialize in- and output ObjectStreams.
    }


// methods ////////////////////////////////////////////////////////////////////

    /**
     * Does blocking infinite and dispatches received messages to the registered message handlers.
     */
    public void listen(){
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    /**
     * Sends the specified message to the associated client.
     * @param message
     *      The message to send.
     */
    public void send( Message message ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public void addMessageHandler( Consumer<Message> handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public void removeMessageHandler( Consumer<Message> handler ){
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public void addDisconnectedHandler( Runnable handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }

    public void setPlayerName( String playerName ) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     * Cleans up the owned resources.
     */
    @Override
    public void close() {
        /*try{
            socket.close();
        }catch( IOException e ){
            String msg = "Failed to close socket.";
            if( logger.isDebugEnabled() ){
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                final PrintWriter writer = new PrintWriter( buffer );
                e.printStackTrace( writer );
                writer.close();
                msg += "\n"+ buffer.toString();
            }
            logger.error( msg );
        }
        */
    }

}
