package application.network.impl.a.internalMessages;

import application.network.api.Message;


/**
 * Ist eine interne Nachricht. Sie wird in der netzwerkschickt verarbeitet und nicht an den client code delegiert. Alle
 * internen Nachrichten implementieren dieses interface hier. So kann die Netzwerkschicht mit instanceof alle internen
 * Nachrichten zuverlässig erkennen.
 * Diese Nachrichten dürfen nicht an den client code geschickt werden.
 */
public interface InternalMessage extends Message {


// memory /////////////////////////////////////////////////////////////////////


// constructors ///////////////////////////////////////////////////////////////


// methods ////////////////////////////////////////////////////////////////////


}
