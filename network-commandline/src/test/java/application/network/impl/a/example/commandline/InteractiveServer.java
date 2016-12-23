package application.network.impl.a.example.commandline;

import application.network.api.Message;
import application.network.api.NetworkModule;
import application.network.api.server.Server;
import application.network.impl.a.NetworkModuleA;
import application.network.impl.a.example.simpleCli.SimpleCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InteractiveServer {

    private static final Logger logger = LoggerFactory.getLogger( InteractiveServer.class );
    private final NetworkModule networkModule = new NetworkModuleA();
    private final SharedFunctions shared = new SharedFunctions( "NETWORK-SERVER: " );


    public static void main( String args[] ) {
        new InteractiveServer().run();
    }

    private void run(){
        // Prepare a server
        Server server = networkModule.createServer();
        server.addClientConnectedHandler( ( playerName )->{
            // Den namen SERVER wollen wir nicht erlauben.
            return !"SERVER".equalsIgnoreCase(playerName);
        });
        server.addMessageHandler( (Message msg , String name )->{
            shared.writeToStdout( "Received from "+name+": "+ shared.messageToString(msg)  );
        });
        server.addClientDisconnectedHandler( ( playerName )->{
            shared.writeToStdout( "Player '"+playerName+"' disconnected." );
        });

        SimpleCli cli = new SimpleCli();
        cli.setPrompt( "Server> " );
        cli.setHandler( ( cmd )->{
            try{
                String[] parts = cmd.split( " " );
                if( parts.length == 0 ){
                    return true;
                }else if( "broadcast".equals(parts[0]) ){
                    String[] args = new String[ parts.length-1 ];
                    System.arraycopy( parts , 1 , args , 0 , args.length );
                    Message msg = shared.createMessage( args , "SERVER" );
                    server.broadcast( msg );
                }else if( "exit".equals(parts[0]) ){
                    server.shutdown();
                    return false;
                }else if( "help".equals(parts[0]) ){
                    shared.writeToStdout( ""
                            +"Available commands:\n"
                            +"  broadcast <MessageType> <args...>\n"
                            +"      Sends the specified message to all connected clients.\n"
                            +"  exit\n"
                            +"      Exit the program.\n"
                            +"  listen <port>\n"
                            +"      Macht den server bereit um nachrichten auszutauschen.\n"
                            +"  shutdown\n"
                            +"      Schliesst den Socket.\n"
                            +"  sendTo <playerName> <MessageType> <args...>\n"
                            +"      Available messages:\n"
                            +"      DropBomb <id> <x> <y>\n"
                            +"      BombDropped <x> <y>\n"
                            +"      BombExploded\n"
                            +"      ClientLogin <playerName>\n"
                            +"      ClientLogout\n"
                            +"      DropBomb <x> <y>\n"
                            +"      GameOver <winnerName>\n"
                            +"      LoginFailed [<reason>]\n"
                            +"      LoginSucceeded <x> <y>\n"
                            +"      PlayerHit <playerName>\n"
                            +"      PlayerJoined <x> <y>\n"
                            +"      PlayerMoved <direction>\n"
                            +"          Where <direction> can be one of: UP, DOWN, LEFT, RIGHT\n"
                            +"      StartGame\n"
                            +"      UpdateGame\n"
                    );
                }else if( "listen".equals(parts[0]) ){
                    if( parts.length < 2 ) throw new IllegalArgumentException( "No port specified." );
                    int port = Integer.parseInt( parts[1] );
                    server.listen( port );
                }else if( "shutdown".equals(parts[0]) ){
                    server.shutdown();
                }else if( "sendTo".equals(parts[0]) ){
                    if( parts.length < 2 ) throw new IllegalArgumentException( "No receiver specified." );
                    String playerName = parts[1];
                    String[] args = new String[ parts.length-2 ];
                    System.arraycopy( parts , 2 , args , 0 , args.length );
                    Message msg = shared.createMessage( args , "SERVER" );
                    server.send( msg , playerName );
                }else{
                    shared.writeToStdout( "Unknown command "+ parts[0] );
                }
            }catch( Exception e ){
                e.printStackTrace();
            }
            return true;
        });
        cli.run();
        server.shutdown();
    }

}
