package application.network.impl.a.server.connection;

import application.network.api.Message;

import java.util.UUID;


public interface Connection {

    void send( Message msg );

    UUID getId();

    String getPublicId();

    void setPublicId();

    void shutdown();

}
