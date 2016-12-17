package application.network.impl.a.client.connection;

import application.network.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Diese Verbindungsklasse kommuniziert mittels ObjectStreams.
 */
public class ConcreteConnection implements Connection {

    private final Socket socket;
    private final ConnectionHandler handler;
    private final ObjectInput in;
    private final ObjectOutput out;
    private final Thread messageListener;
    private final ExecutorService messageSendQueue;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected ConcreteConnection(Socket serverConnection, ConnectionHandler handler) throws IOException
    {
        this.socket = serverConnection;
        this.handler = handler;
        this.in = new ObjectInputStream(serverConnection.getInputStream());
        this.out = new ObjectOutputStream(serverConnection.getOutputStream());
        this.messageListener = new Thread(this::readMessages);
        this.messageSendQueue = Executors.newSingleThreadExecutor();
    }

    /**
     * Diese Methode sendet Nachrichten zum Server.
     * Die Nachrichten werden asynchron in der übermittelten Reihenfolge gesendet.
     *
     * @param message Die Nachricht welche zum Server gesendet werden soll.
     *
     * @throws NullPointerException Wenn die Nachricht null ist.
     * @throws IllegalStateException Wenn die Verbindung bereits heruntergefahren wurde.
     */
    @Override
    public void send(Message message) {

        if(message == null)
            throw new NullPointerException("Die Message darf nicht null sein.");

        if(messageSendQueue.isShutdown())
            throw new IllegalStateException("Die ausgehende Verbindung wurde bereits heruntergefahren.");

        submitSendTask(message);
    }

    private void submitSendTask(Message message)
    {
        messageSendQueue.submit(
                () -> {
                    try
                    {
                        out.writeObject(message);
                        handler.messageSend(message);
                    }
                    catch (IOException ex)
                    {
                        log.warn("Konnte Nachricht nicht zu Server senden.");
                    }
                });
    }

    /**
     * Schliest die Verbindung zum Server.
     */
    @Override
    public void shutdown() {

        shutdownIoOperations();
        closeSocket();
        closeInput();
        closeOutput();
        handler.connectionClosed();
    }

    private void shutdownIoOperations()
    {
        messageListener.interrupt();
        messageSendQueue.shutdown();
    }

    private void closeSocket()
    {
        try
        {
            socket.close();
        }
        catch(IOException ex)
        {
            log.error("Konnte Socket auf Grund einer IOException nicht schliesen!");
        }
    }

    private void closeInput()
    {
        try
        {
            in.close();
        }
        catch(IOException ex)
        {
            log.error("Konnte ObjectInput auf Grund einer IOException nicht schliesen!");
        }
    }

    private void closeOutput()
    {
        try
        {
            out.close();
        }
        catch(IOException ex)
        {
            log.error("Konnte ObjectOutput auf Grund einer IOException nicht schliesen!");
        }
    }

    /**
     * Liest eine Nachricht synchron von der aktuellen Verbindung.
     * Diese Methode darf nur vor dem startReading Methodenaufruf verwendet werden.
     *
     * @return Die empfangene Nachricht.
     * @throws IllegalStateException Wenn die startReading Methode bereits aufgrufen wurde.
     * @throws InterruptedException Wenn der aktuelle Thread unterbrochen wurde.
     */
    @Override
    public Message read() throws InterruptedException {
        try
        {
            if(messageListener.getState() == Thread.State.NEW)
                return readMessage();
            else
                throw new IllegalStateException("Der asynchrone Lesevorgang wurde bereits gestartet daher sind keine synchrone Zugriffe mehr erlaubt.");
        }
        catch (InterruptedIOException ex)
        {
            throw new InterruptedException("Der unterliegende Stream wurde waerend des Leseprozesses geschlossen.");
        }

    }

    /**
     * Startet den asynchronen Leseprozess.
     * Es wird nach dem Aufruf dieser Methode fuer jede Nachricht die messageReceived
     * Methode des registrierten ConnectionHandlers aufgerufen.
     *
     * @throws IllegalStateException Wenn der Lesevorgang bereits gestartet wurde.
     */
    public void startReading() {
        if(messageListener.getState() == Thread.State.NEW)
            messageListener.start();
        else
            throw new IllegalStateException("Lesevorgang wurde bereits gestartet oder beendet.");
    }

    /**
     * true wenn auf neu eingehende Nachrichten gewartet wird.
     * @return true wenn auf neu eingehende Nachrichten gewartet wird.
     */
    boolean isReading()
    {
        log.trace("Momentaner Lesestatus ist " + messageListener.getState().name());
        return messageListener.getState() != Thread.State.NEW && messageListener.getState() != Thread.State.TERMINATED;
    }

    /**
     * Liest Nachrichten bis der aktuelle Thread unterbrochen wird
     * oder die Verbindung geschlossen wird.
     */
    private void readMessages()
    {
        try
        {
            while(socket.isConnected())
            {
                Message message = readMessage();
                if (message == null)
                    continue;

                handler.messageReceived(message);
            }
        }
        catch (InterruptedIOException ex)
        {
            //zu diesem Zeitpunkt ist mindestens einer der Streams nicht mehr einsatzfaehig daher wird die Verbindung geschlossen.
            log.debug(ex.getMessage());
            shutdown();
        }
        catch (InterruptedException ex)
        {
            log.debug(ex.getMessage());
        }
    }

    /**
     * Liest eine Nachricht und giebt diese zurueck.
     * Sollte der Lesevorgang unterbrochen werden gibt die Methode null zurueck.
     *
     * @return Die erhaltenen Nachricht oder null wenn ein nicht kritischer Fehler aufgetreten ist.
     * @throws InterruptedException     Wenn der Thread unterbrochen wurde.
     * @throws InterruptedIOException   Wenn die unterliegenden Streams am ende sind oder die Streams korrupt sind.
     */
    private Message readMessage() throws InterruptedException, InterruptedIOException
    {
        try
        {
            //DIRTY: der messageListener thread ist zu greedy unter gewissen Umstaenden daher sleep von 5ms damit der interrupt status gesetzt werden kann von anderen threads.
            Thread.sleep(5);
            if(messageListener.isInterrupted())
            {
                throw new InterruptedException("Der Nachrichtenlesevorgang wurde unterbrochen.");
            }

            return (Message) in.readObject();
        }
        catch(ClassNotFoundException ex)
        {
            log.warn("Nachricht konnte nicht deserialisiert werden und wird ignoriert. Verwenden Server und Client die gleiche Version der Netzwerkimplementierung A?");
        }
        catch(EOFException ex)
        {
            log.debug("Streamende erreicht beende server verbindung");
            throw new InterruptedIOException("Der Nachrichtenlesevorgang wurde unterbrochen, auf Grund des erreichten Streamendes.");
        }
        catch (StreamCorruptedException ex)
        {
            log.error("Der InputStream ist korrupt schliese Verbindung.");
            throw new InterruptedIOException("Der unterliegende Stream ist korrupt.");
        }
        catch(IOException ex)
        {
            log.debug("Unbekannte IO exception in readMessage.");
        }
        catch (ClassCastException ex)
        {
            log.warn("Ignoriere inkompatible Nachricht. Nur Nachrichten welche das Message Interface implementieren werden von der Netzwerkimplementierung A unterstuetzt.");
        }

        //workaround da java keine tailcall optimierung behaerst und ich keine zweite schlaufe verwenden moechte.
        return null;
    }
}
