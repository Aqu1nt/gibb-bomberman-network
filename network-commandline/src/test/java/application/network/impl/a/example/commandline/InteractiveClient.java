package application.network.impl.a.example.commandline;

import application.network.api.NetworkModule;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.impl.a.NetworkModuleA;
import application.network.impl.a.example.simpleCli.SimpleCli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InteractiveClient {

    private static final Logger logger = LoggerFactory.getLogger( InteractiveClient.class );
    private static final NetworkModule networkModule = new NetworkModuleA();
    private final SharedFunctions shared = new SharedFunctions( "NETWORK-CLIENT: " );


    public static void main( String args[] ) {
        new InteractiveClient().goInteractive();
    }

    private void goInteractive(){
        final ServerProxy client = networkModule.createClient();
        final SimpleCli cli = new SimpleCli();
        String[] name = new String[]{ "" };
        cli.setPrompt( "InteractiveClient> " );
        client.addMessageHandler( msg -> {
            shared.writeToStdout( "Received "+ shared.messageToString(msg) );
        });
        client.addServerDisconnectedHandler( ()->{
            shared.writeToStdout( "Disconnect event received." );
            cli.setPrompt( "InteractiveClient> " );
        });
        cli.setHandler( ( cmd )->{
            try{
                String[] parts = cmd.split( " " );
                if( "exit".equals(parts[0]) ){
                    return false;
                }else if( "connect".equals(parts[0]) ){
                    if( parts.length != 4 ){
                        shared.writeToStdout( "Wrong count of arguments: "+(parts.length-1)+". See help." );
                    }else{
                        String host = parts[1];
                        int port = Integer.parseInt( parts[2] );
                        name[0] = parts[3];
                        try{
                            client.connect( name[0] , host , port );
                            cli.setPrompt( "InteractiveClient "+ name[0] +"> " );
                        }catch( ClientIdInUseException e ){
                            shared.writeToStdout( "ERR: Failed to connect to server. Player name already used." );
                        }catch( LobbyFullException e ){
                            System.err.println( "ERR: Failed to connect to server. Lobby full." );
                        }
                    }
                }else if( cmd.equals("disconnect") ){
                    cli.setPrompt( "InteractiveClient> " );
                    client.disconnect();
                }else if( cmd.equals("help") ){
                    shared.writeToStdout( "Available commands:\n"
                        +"  connect <host> <port> <playerName>\n"
                        +"      Connects to the hardcoded host and does login with <playerName>.\n"
                        +"  disconnect\n"
                        +"      Disconnects from the currently connected server.\n"
                        +"  exit\n"
                        +"      Exits the program.\n"
                        +"  send <MessageType> <args...>\n"
                        +"      Available messages:"
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
                }else if( cmd.startsWith("send ") ){
                    String[] args = new String[ parts.length-1 ];
                    System.arraycopy( parts , 1 , args , 0 , args.length );
                    client.send( shared.createMessage(args,name[0]) );
                }else{
                    shared.writeToStdout( "ERR: Unknown command: "+ parts[0] );
                }
            }catch( Exception e ){
                logger.error( "There was an exception:" , e );
            }
            return true;
        });
        cli.run();
        client.disconnect();
    }

}
