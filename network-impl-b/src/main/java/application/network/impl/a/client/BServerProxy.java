package application.network.impl.a.client;

import application.network.api.Message;
import application.network.api.client.ClientIdInUseException;
import application.network.api.client.LobbyFullException;
import application.network.api.client.ServerProxy;
import application.network.impl.a.internal.InternalClientIdMessage;
import application.network.impl.a.internal.InternalClientIdResponse;
import application.network.impl.a.internal.InternalMessage;
import application.network.impl.a.utils.NetworkConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class BServerProxy extends NetworkConnection implements ServerProxy
{
    private CompletableFuture<InternalClientIdResponse> clientIdResponse = new CompletableFuture<>();

    private final Set<Consumer<Message>> messageHandlers = Collections.synchronizedSet(new HashSet<>());
    private final Set<Runnable> serverDisconnectedHandlers = Collections.synchronizedSet(new HashSet<>());

    public BServerProxy()
    {
        handle(this::handleIncomingMessage);
        handleInternal(this::handleClientIdResponse);
    }

    @Override
    public void connect(String clientId, String ip, int port) throws IOException, ClientIdInUseException, LobbyFullException
    {
        open(new Socket(ip, port));
        send(new InternalClientIdMessage().setClientId(clientId));
        verifyConnection();
    }

    public void verifyConnection() throws ClientIdInUseException, LobbyFullException, IOException
    {
        clientIdResponse = new CompletableFuture<>();
        try
        {
            InternalClientIdResponse response = clientIdResponse.get(1, TimeUnit.SECONDS);
            if (response.isClientIdInUse()) {
                throw new ClientIdInUseException();
            }
            else if (response.isClientRejected()) {
                throw new LobbyFullException();
            }
        }
        catch (ExecutionException | InterruptedException | TimeoutException e)
        {
            throw new IOException("Did not retrieve a response from the server", e);
        }
    }

    public void handleClientIdResponse(InternalMessage internalMessage)
    {
        if (internalMessage instanceof InternalClientIdResponse)
        {
            clientIdResponse.complete((InternalClientIdResponse) internalMessage);
        }
    }

    public void handleIncomingMessage(Message message)
    {
        messageHandlers.forEach(h -> h.accept(message));
    }

    @Override
    public void disconnect()
    {
        serverDisconnectedHandlers.forEach(Runnable::run);
    }

    @Override
    public synchronized void close()
    {
        super.close();
        disconnect();
    }

    @Override
    public void addMessageHandler(Consumer<Message> handler)
    {
        messageHandlers.add(handler);
    }

    @Override
    public void addServerDisconnectedHandler(Runnable handler)
    {
        serverDisconnectedHandlers.add(handler);
    }
}
