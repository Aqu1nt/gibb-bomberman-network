package application.network.impl.a.server;

import application.network.api.Message;
import application.network.api.server.Server;
import application.network.impl.a.server.event.EventManager;
import application.network.impl.a.server.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


class ConcreteServer implements Server, Closeable {


// memory /////////////////////////////////////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger( ConcreteServer.class );
    private final SecurityManager securityManager;
    private final ConnectionHandler connectionHandler;
    private final EventManager eventManager;
    private final ConnectionFactory connectionFactory;
    private final ExecutorService executorService;
    private boolean hasShutdownRequest = false;
    private ServerSocket socket;


// constructors ///////////////////////////////////////////////////////////////

    ConcreteServer(
            SecurityManager securityManager,
            ConnectionHandler connectionHandler,
            EventManager eventManager,
            ConnectionFactory connectionFactory,
            ExecutorService executorService
    ){
        this.securityManager = securityManager;
        this.connectionHandler = connectionHandler;
        this.eventManager = eventManager;
        this.connectionFactory = connectionFactory;
        this.executorService = executorService;
    }


// methods ////////////////////////////////////////////////////////////////////

    @Override
    public void listen( final int port ) throws IOException, IllegalStateException {
        synchronized( this ){
            if( socket != null ) throw new IllegalStateException("There is already a socket.");
            socket = new ServerSocket( port );
        }
        executorService.execute( ()->{
            logger.trace( "Thread '"+Thread.currentThread().getName()+"' spawned to listen on server socket." );
            logger.info( "Server is listening on port "+ port +" ..." );
            serverMainLoop();
        });
    }

    @Override
    public void shutdown() {
        synchronized( this ){
            hasShutdownRequest = true;
        }
    }

    @Override
    public void send( Message message , String playerName ) {
        connectionHandler.messageSend( message , playerName );
    }

    @Override
    public void broadcast( Message message ) {
        connectionHandler.broadcast( message );
    }

    @Override
    public void addMessageHandler( BiConsumer<Message,String> handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" ); // TODO: implement this method.
    }

    @Override
    public void addClientConnectedHandler( Function<String,Boolean> handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" ); // TODO: implement this method.
    }

    @Override
    public void addClientDisconnectedHandler( Consumer<String> handler ) {
        throw new UnsupportedOperationException( "Not implemented yet" ); // TODO: implement this method.
    }

    @Override
    public void close() throws IOException {
        if( socket != null ) socket.close();
        // TODO: Close all the client sockets.
    }

    /**
     * Blocks until this instance has a shutdown request.
     */
    private void serverMainLoop(){
        while( true ){
            Socket clientSocket;
            synchronized( this ){
                if( hasShutdownRequest ) break;
            }

            // Wait for next message.
            try{
                clientSocket = socket.accept();
            }catch( IOException e ){
                // TODO: What to do here?
                String msg = "Unexpected exception. Shutting down server.";
                logger.error( msg , e );
                throw new RuntimeException( msg , e );
            }

            // Delegate work to other thread to continue listening for new clients.
            executorService.execute( ()->{
                logger.trace( "Thread '"+Thread.currentThread().getName()+"' now takes care of newly connected client." , clientSocket );
                clientHandler( clientSocket );
            });
        }
    }

    /**
     * Delegates the handling of the clients to the connection handler.
     * @param clientSocket
     *      The socket of the client to handle.
     */
    private void clientHandler( Socket clientSocket ){
        final Connection connection = connectionFactory.create( clientSocket );
        connectionHandler.connectionCreated( connection );
    }

}
