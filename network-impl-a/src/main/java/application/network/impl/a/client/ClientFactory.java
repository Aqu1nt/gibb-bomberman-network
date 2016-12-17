package application.network.impl.a.client;

import application.network.api.client.ServerProxy;
import application.network.impl.a.client.connection.ConcreteConnectionFactory;
import application.network.impl.a.client.connection.ConcreteConnectionHandler;
import application.network.impl.a.client.connection.ConnectionFactory;
import application.network.impl.a.client.connection.ConnectionHandler;
import application.network.impl.a.client.event.ConcreteEventHandler;
import application.network.impl.a.client.event.EventDispatcher;
import application.network.impl.a.client.event.EventManager;

/**
 * Factory fuer die Erstellung von Clients.
 */
public class ClientFactory {

    private ConnectionFactory createConnectionFactory(ConnectionHandler connectionHandler)
    {
        return new ConcreteConnectionFactory(connectionHandler);
    }

    private ConnectionHandler createConnectionHandler(EventDispatcher eventDispatcher)
    {
        return new ConcreteConnectionHandler(eventDispatcher);
    }

    private EventManager createEventManager()
    {
        return new ConcreteEventHandler();
    }

    public ServerProxy getInstance()
    {
        EventManager eventManager = createEventManager();
        ConnectionHandler connectionHandler = createConnectionHandler(eventManager);
        ConnectionFactory connectionFactory = createConnectionFactory(connectionHandler);

        return new ConcreteClient(
                connectionFactory,
                connectionHandler,
                eventManager
        );
    }
}
