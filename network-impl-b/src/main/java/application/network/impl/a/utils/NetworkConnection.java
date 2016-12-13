package application.network.impl.a.utils;

import application.network.api.Message;
import application.network.impl.a.internal.InternalMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static application.network.impl.a.utils.StaticHelpers.suppress;

/**
 * Super klasse für alle Verbindungen über das Netzwerk, ermöglicht das empfangen und versenden
 * von Messages
 */
@Slf4j
public class NetworkConnection implements Runnable
{
    private Socket socket;
    private Thread thread;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final Set<Consumer<Message>> messageHandlers = new HashSet<>();
    private final Set<Consumer<InternalMessage>> internalHandlers = new HashSet<>();

    public synchronized void open(@NonNull Socket socket)
    {
        if (isOpen()) {
            log.error("Network connection is already open");
            throw new IllegalStateException("Network connection is already open");
        }
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            log.info("Opening connection to "+socket.getRemoteSocketAddress());
            this.thread = new Thread(this);
            this.thread.setName("conn-"+socket.getRemoteSocketAddress());
            this.thread.start();
        } catch (IOException e) {
            log.error("Could not open streams on socket ", e);
            throw new RuntimeException(e);
        }
    }

    public synchronized void close()
    {
        if (isOpen()) { // Do nothing if not running
            log.info("Closing NetworConnection to "+socket.getRemoteSocketAddress());
            suppress(() -> { this.socket.close(); return null; });
            this.thread = null;
            this.socket = null;
        }
    }

    @Override
    public void run() {
        log.info("Network connection to "+socket.getRemoteSocketAddress().toString()+" opened, reading objects");
        while (isOpen()) {
            try {
                Object object = inputStream.readObject();
                dispatch(object);
            }
            catch (EOFException e) {
                log.info("Network connection closed remotely");
                close();
            }
            catch (Exception e) {
                log.error("Could not read object from input stream", e);
            }
        }
    }

    public void dispatch(Object object)
    {
        if (object instanceof InternalMessage) {
            internalHandlers.forEach(h -> h.accept(((InternalMessage) object)));
        }
        else if (object instanceof Message) {
            messageHandlers.forEach(h -> h.accept(((Message) object)));
        }
        else {
            log.error("Could not dispatch object "+object+" because its neither a Message or an InternalMessage");
        }
    }

    public synchronized void send(@NonNull Message message)
    {
        if (!isOpen()) {
            throw new IllegalStateException("Connection is closed");
        }
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            log.warn("Sending of message "+message+" failed", e);
        }
    }

    public void handle(Consumer<Message> handler)
    {
        this.messageHandlers.add(handler);
    }

    public void handleInternal(Consumer<InternalMessage> handler)
    {
        this.internalHandlers.add(handler);
    }

    public boolean isOpen()
    {
        return socket != null;
    }
}
