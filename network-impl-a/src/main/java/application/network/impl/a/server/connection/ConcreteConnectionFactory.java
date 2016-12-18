package application.network.impl.a.server.connection;

import java.net.Socket;


public class ConcreteConnectionFactory implements ConnectionFactory {

    public ClientHandle createClientHandle( Socket socket ){
        return new ClientHandle( socket );
    }

}
