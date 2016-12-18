package application.network.impl.a.server.connection;

import java.net.Socket;


public interface ConnectionFactory {

    ClientHandle createClientHandle( Socket socket );

}
