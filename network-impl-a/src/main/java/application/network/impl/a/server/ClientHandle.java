package application.network.impl.a.server;

import java.net.Socket;


/**
 * Mit diesem handle kann der server einen client verwalten.
 */
public class ClientHandle {


// memory /////////////////////////////////////////////////////////////////////

    private Socket socket;
    private String playerName;


// constructors ///////////////////////////////////////////////////////////////

    public ClientHandle( Socket socket ){
        throw new UnsupportedOperationException( "Not implemented yet" );
    }


// methods ////////////////////////////////////////////////////////////////////

    public Socket getSocket() {
        return socket;
    }

    public ClientHandle setSocket( Socket socket ) {
        this.socket = socket;
        return this;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ClientHandle setPlayerName( String playerName ) {
        this.playerName = playerName;
        return this;
    }

}
