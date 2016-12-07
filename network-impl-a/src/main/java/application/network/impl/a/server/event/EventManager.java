package application.network.impl.a.server.event;

import application.network.api.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public interface EventManager extends EventDispatcher {

    void addMessageReceivedObserver( BiConsumer<Message,String> messageHandler );

    void addClientDisconnectedObserver( Consumer<String> clientDisconnectedHandler );

}
