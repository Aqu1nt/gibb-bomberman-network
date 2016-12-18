package application.network.impl.a.server.connection;

import application.network.api.Message;


public interface Connection {

    void send( Message msg );

}
