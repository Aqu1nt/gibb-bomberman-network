package application.network.impl.a.server;


import application.network.api.Message;
import application.network.api.server.Server;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static application.network.impl.a.utils.StaticHelpers.suppress;

@Slf4j
public class BServer implements Server, Runnable
{
    private final Object SOCKET_LOCK = new Object();

    /**
     * The thread which is accepting new clients
     */
    private Thread executingThread;

    /**
     * The server socket that is used to accept new clients
     */
    private ServerSocket socket;

    /**
     * Liste mit allen fest verbundenen connections
     */
    @Getter
    private final Set<BServerConnection> connections = new HashSet<>();

    /**
     * Liste mit allen handlern welche aufgeruft werden bei einer eingehenden Nachricht
     */
    private final Set<BiConsumer<Message, String>> messageHandlers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Liste mit allen handlern welche aufgeruft werden sobald sich ein neuer
     * client verbinden möchte
     */
    private final Set<Function<String, Boolean>> clientConnectedHandlers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Liste mit allen handlern welche aufgeruft werden sobald eine Verbindun geschlossen wird
     */
    private final Set<Consumer<String>> clientDisonnectedHandlers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Funktion welche eine neue connection erstellt
     */
    private BiFunction<Socket, BServer, BServerConnection> connectionFactory = null;

    public BServer()
    {
        this(BServerConnection::new);
    }

    public BServer(BiFunction<Socket, BServer, BServerConnection> connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void listen(int port) throws IOException, IllegalStateException
    {
        synchronized (SOCKET_LOCK)
        {
            if (socket != null)
            {
                log.error("Port "+port+" is already opened by another server");
                throw new IllegalStateException("Server is already running");
            }
            executingThread = new Thread(this);
            executingThread.setName("network-server-"+port);
            socket = new ServerSocket(port);
            executingThread.start();
            log.info("Starting new Network Server on port "+port);
        }
    }

    @Override
    public void run()
    {
        log.info("Server thread started up, accepting new clients");
        while(socket != null && !socket.isClosed())
        {
            Socket clientSocket = suppress(() -> socket.accept());
            if (clientSocket != null)
            {
                connectionFactory.apply(clientSocket, this);
            }
        }
    }

    @Override
    public void shutdown()
    {
        synchronized (SOCKET_LOCK)
        {
            if (socket != null)
            {
                suppress(() -> { socket.close(); return null; });
            }
            connections.forEach(BServerConnection::close);
            connections.clear();
            socket = null;
        }
    }

    public void registerConnection(BServerConnection connection)
    {
        synchronized (connections)
        {
            if (connection.getClientId() == null || isClientIdInUse(connection.getClientId()))
            {
                throw new IllegalStateException("Client id ("+connection.getClientId()+") is either null or already existing");
            }
            log.info("Server registered new client with ClientID "+connection.getClientId());
            connections.add(connection);
        }
    }

    public void unregisterConnection(BServerConnection connection)
    {
        synchronized (connections)
        {
            if (connections.contains(connection))
            {
                log.info("Server is unregistering client with ClientID "+connection.getClientId());
                clientDisonnectedHandlers.forEach(h -> h.accept(connection.getClientId()));
                connections.remove(connection);
            }
        }
    }

    public boolean isClientIdInUse(@NonNull String clientId)
    {
        synchronized (connections)
        {
            return connections.stream().anyMatch(c -> clientId.equals(c.getClientId()));
        }
    }

    public boolean isClientAccepted(String clientId)
    {
        for (Function<String, Boolean> clientConnectedHandler : clientConnectedHandlers)
        {
            if (!clientConnectedHandler.apply(clientId))
            {
                return false;
            }
        }
        return true;
    }

    public void handleMessage(BServerConnection connection, Message message)
    {
        // Only do for accepted connections
        if (connections.contains(connection))
        {
            messageHandlers.forEach(h -> h.accept(message, connection.getClientId()));
        }
    }

    @Override
    public void send(@NonNull Message message, @NonNull String clientId)
    {
        BServerConnection connection = connections.stream()
                .filter(c -> c.getClientId().equals(clientId)).findFirst().orElse(null);
        if (connection != null)
        {
            connection.send(message);
        }
        else
        {
            log.warn("Could not send message to "+clientId+" because no connection with this ClientID is registered");
        }
    }

    @Override
    public void broadcast(@NonNull Message message)
    {
        connections.forEach(c -> c.send(message));
    }

    @Override
    public void addMessageHandler(@NonNull BiConsumer<Message, String> handler)
    {
        messageHandlers.add(handler);
    }

    @Override
    public void addClientConnectedHandler(@NonNull Function<String, Boolean> handler)
    {
        clientConnectedHandlers.add(handler);
    }

    @Override
    public void addClientDisconnectedHandler(Consumer<String> handler)
    {
        clientDisonnectedHandlers.add(handler);
    }

}
