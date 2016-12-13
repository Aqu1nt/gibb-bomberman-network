package application.network.impl.a.server;

import application.network.api.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BServerConnection
{
    private String clientId;
    private BServer server;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public BServerConnection(Socket socket, BServer server) throws IOException
    {
        this.server = server;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message)
    {

    }

    public void close()
    {

    }

    public String getClientId()
    {
        return clientId;
    }
}
