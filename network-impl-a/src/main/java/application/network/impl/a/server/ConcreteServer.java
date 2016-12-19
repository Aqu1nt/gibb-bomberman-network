package application.network.impl.a.server;

import application.network.api.Message;
import application.network.api.server.Server;
import application.network.impl.a.message.ClientLoginRequest;
import application.network.impl.a.message.InternalMessage;
import application.network.impl.a.message.LoginFailedMessage;
import application.network.impl.a.message.LoginSuccessMessage;
import application.network.impl.a.server.connection.ClientHandle;
import application.network.impl.a.server.connection.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static application.network.impl.a.message.LoginFailedMessage.LoginFailedReason.LOBBY_FULL;


class ConcreteServer implements Server, Closeable {


// memory /////////////////////////////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger( ConcreteServer.class );
    private final ConnectionFactory connectionFactory;
    private final List<ClientHandle> clients = new ArrayList<>();
    private final List<BiConsumer<Message, String>> messageHandlers = new ArrayList<>();
    private final List<Function<String,Boolean>> clientConnectedHandlers = new ArrayList<>();
    private final ArrayList<Consumer<String>> clientDisconnectedHandlers = new ArrayList<>();
    private boolean hasShutdownRequest = false;
    private ServerSocket socket;
    private Thread listenerThread;


// constructors ///////////////////////////////////////////////////////////////

    ConcreteServer(
            ConnectionFactory connectionFactory
    ){
        this.connectionFactory = connectionFactory;
    }


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void listen( int port ) throws IOException, IllegalStateException {
        Runnable listenerTask = ()->{
            serverMainLoop();
            synchronized( this ){
                listenerThread = null;  // Set back to null if no longer running.
            }
        };
        synchronized( this ){
            if( listenerThread != null ) throw new IllegalStateException("There is already a thread listening.");
            if( socket != null ) throw new IllegalStateException("There is already a socket.");
            socket = new ServerSocket( port );
            listenerThread = new Thread( listenerTask );
            hasShutdownRequest = false;
        }
        listenerThread.start();
    }

    @Override
    public void shutdown() {
        synchronized( this ){
            if( socket != null ){
                try{
                    socket.close();
                }catch( IOException e ){
                    logger.error( "Failed to close socket." , e );
                }
            }
            if( listenerThread != null ) listenerThread.interrupt();
        }
        try{
            if( listenerThread != null ) listenerThread.join();
        }catch( InterruptedException e ){
            logger.error( "Failed to join with listener thread." , e );
        }
        listenerThread = null;

        synchronized( clients ){
            for( ClientHandle client : clients ){
                client.close();
            }
        }
    }

    @Override
    public void send( Message message , String playerName ) {
        ClientHandle client = null;
        synchronized( clients ){
            for( ClientHandle c : clients ){
                if( c.getPlayerName().equals(playerName) ){
                    client = c;
                    break;
                }
            }
        }
        if( client == null ) throw new IllegalArgumentException( "No client with name '"+playerName+"' to send the message to." );
        logger.trace( "Send message {} to client {}." , message , client );
        client.send( message );
    }

    @Override
    public void broadcast( Message message ) {
        synchronized( clients ){
            for( ClientHandle client : clients ){
                client.send( message );
            }
        }
    }

    @Override
    public void addMessageHandler( BiConsumer<Message, String> handler ) {
        synchronized( messageHandlers ){
            messageHandlers.add( handler );
        }
    }

    @Override
    public void addClientConnectedHandler( Function<String,Boolean> handler ) {
        synchronized( clientConnectedHandlers ){
            clientConnectedHandlers.add( handler );
        }
    }

    @Override
    public void addClientDisconnectedHandler( Consumer<String> handler ) {
        synchronized( clientDisconnectedHandlers ){
            clientDisconnectedHandlers.add( handler );
        }
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }

    /**
     * Blocks, receives new clients and delegate them to the {@link #handleNewConnection(Socket)} method.
     */
    private void serverMainLoop(){
        while( !hasShutdownRequest ){
            Socket clientSocket;
            try{
                // Await next client.
                clientSocket = socket.accept();
            }catch( IOException e ){
                logger.error( "Failed to receive next client." , e );
                return;
            }
            new Thread( ()->{
                logger.trace( "Handle newly connected tcp client "+ clientSocket.getInetAddress().getHostAddress() +":"+clientSocket.getPort() );
                handleNewConnection( clientSocket );
            }).start();
        }
    }

    /**
     * Handles a client intermediately after he connected to this server.
     * @param socket
     *      The new client.
     */
    private void handleNewConnection( Socket socket ){
        ClientHandle clientHandle = connectionFactory.createClientHandle( socket );
        clientHandle.addMessageHandler( ( Message msg )->{
            handleMessage( clientHandle , msg );
        });
        clientHandle.addDisconnectedHandler( ()->{
            // Remove obsolete client.
            boolean wasKnown;
            synchronized( clients ){
                wasKnown = clients.remove( clientHandle );
            }
            if( wasKnown ){
                // Client was logged in. Notify about disconnect.
                synchronized( clientDisconnectedHandlers ){
                    for( Consumer<String> handler : clientDisconnectedHandlers ){
                        try{
                            handler.accept( clientHandle.getPlayerName() );
                        }catch( RuntimeException e ){
                            logger.warn( "A disconnect handler trowed an exception:" , e );
                        }
                    }
                }
            }
        });
        clientHandle.listen();
    }

    /**
     * Handles the received message. If the message is an internal message it will delegate it to the internal message
     * handling method. Else it will propagate the message to the registered handlers.
     * @param clientHandle
     *      The client handle where the message came from.
     * @param msg
     *      The received message.
     */
    private void handleMessage( ClientHandle clientHandle , Message msg ){
        if( msg instanceof InternalMessage ){
            logger.trace( "Handle internal Message of type '"+msg.getClass().getName()+"'" );
            handleInternalMessage( clientHandle , msg );
        }else{
            logger.trace( "Prepare external message for delegation" );
            synchronized( clients ){
                if( !clients.contains(clientHandle) ){
                    logger.info( "Message dropped. Client isn't authenticated." );
                    clientHandle.send( new LoginFailedMessage().setReason(LoginFailedMessage.LoginFailedReason.ACCESS_DENIED) );
                    return;
                }
            }
            synchronized( messageHandlers ){
                logger.trace( "Delegate message of type '"+msg.getClass().getName()+"' to "+ messageHandlers.size() +" registered handlers." );
                for( BiConsumer<Message,String> handler : messageHandlers ){
                    handler.accept( msg , clientHandle.getPlayerName() );
                }
            }
        }
    }

    /**
     * Handles internal messages and sends the responses to the client.
     * @param clientHandle
     *      The client from the message was received.
     * @param msg
     *      The message that was received.
     */
    private void handleInternalMessage( ClientHandle clientHandle , Message msg ){
        if( msg instanceof ClientLoginRequest ){
            ClientLoginRequest clientLoginRequest = (ClientLoginRequest)msg;
            if( clientHandle.getPlayerName() != null ){
                logger.warn( "Client '"+ clientHandle.getPlayerName() +"' attempt to authenticate twice." );
                return;
            }
            String playerName = clientLoginRequest.getClientId();

            // Check already existing name.
            clientHandle.setPlayerName( playerName );
            synchronized( clients ){
                if( clients.stream().anyMatch(clientHandle::equalsInName) ){
                    // There is already a client registered using this name.
                    clientHandle.setPlayerName( null );
                    logger.info( "Name '"+ playerName +"' already in use. Reject client." );
                    clientHandle.send( new LoginFailedMessage().setReason(LoginFailedMessage.LoginFailedReason.NAME_ALREADY_USED) );
                    clientHandle.close();
                    return;
                }
            }

            // Ask connectedHandlers whether they all accept the new client.
            boolean accepted = true;
            synchronized( clientConnectedHandlers ){
                logger.trace( "Ask " +clientConnectedHandlers.size() +" predicates what to do with new player." );
                for( Function<String,Boolean> handler : clientConnectedHandlers ){
                    if( handler != null && !handler.apply(playerName) ){
                        accepted = false;
                        break;
                    }
                }
            }
            // If no handler is registered, 'accepted' will stay true. This fulfills the interfaces specification.
            logger.debug( "Client '"+ playerName +"' "+ (accepted?"accepted":"rejected") +"." );
            if( accepted ){
                clients.add( clientHandle );
                clientHandle.send( new LoginSuccessMessage() );
            }else{
                clientHandle.send( new LoginFailedMessage().setReason(LOBBY_FULL) );
            }
        }else{
            throw new UnsupportedOperationException( "Failed to handle internal message of type '"+msg.getClass().getName()+"'. Nothing implemented for this type." );
        }
    }

}
