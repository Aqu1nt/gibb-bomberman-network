package application.network.impl.a.server;

import application.network.impl.a.internal.InternalClientIdMessage;
import application.network.impl.a.internal.InternalClientIdResponse;
import application.network.impl.a.internal.InternalMessage;
import application.network.impl.a.utils.NetworkConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class BServerConnection extends NetworkConnection
{
    @Getter
    private String clientId;

    private final BServer server;

    public BServerConnection(Socket socket, BServer server)
    {
        this(socket, server, true);
    }

    public BServerConnection(Socket socket, BServer server, boolean open)
    {
        this.server = server;
        if (open) open(socket);
        handle(msg -> server.handleMessage(this, msg));
        handleInternal(this::handleClientIdMessage);
    }

    public void handleClientIdMessage(InternalMessage internalMessage)
    {
        if (internalMessage instanceof InternalClientIdMessage)
        {
            InternalClientIdMessage clientIdMessage = (InternalClientIdMessage) internalMessage;
            String clientId = clientIdMessage.getClientId();
            log.info("Client wants to register itself with ClientID "+clientId);
            if (server.isClientIdInUse(clientIdMessage.getClientId())) // ID already in use
            {
                log.info("Client("+clientId+") failed to register itself because ClientID "+clientId+" is already taken");
                send(new InternalClientIdResponse().setClientIdInUse(true));
            }
            else if (!server.isClientAccepted(clientId)) // Rejected
            {
                log.info("Client("+clientId+") failed to register itself because server rejected the client");
                send(new InternalClientIdResponse().setClientRejected(true));
            }
            else // Success
            {
                log.info("Successfully registered client with ClientID "+clientId);
                this.clientId = clientId;
                server.registerConnection(this);
                send(new InternalClientIdResponse());
            }
        }
    }

    @Override
    public void close()
    {
        log.info("Closing connection to "+clientId);
        if (getClientId() != null) {
            server.unregisterConnection(this);
        }
        super.close();
    }
}
