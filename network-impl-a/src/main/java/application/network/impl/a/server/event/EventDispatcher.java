package application.network.impl.a.server.event;

import application.network.api.Message;


public interface EventDispatcher {

    void fireMessageReceived( Message msg , String playerName );

    void fireClientDisconnected( String playerName );

}
