package application.network.impl.a.server.connection;

import application.network.api.Message;
import application.network.impl.a.server.security.SecurityContext;


public interface ConnectionHandler {

    void connectionCreated( Connection connection );

    void messageReceived( Message msg , Connection connection );

    void messageSend( Message msg , Connection connection );

    void exceptionThrown( Connection connection , Exception exception );

    void connectionClosed( Connection connection );

    void send( Message msg , String playerName );

    void broadcast( Message msg );

    void setSecurityContext( SecurityContext securityContext );

}
