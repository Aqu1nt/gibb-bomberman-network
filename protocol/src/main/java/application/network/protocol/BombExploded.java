package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird verwendet um allen Clients mitzuteilen das die Bombe mit der gegebenen id explodiert ist.
 */
public class BombExploded implements Message {

    private int id;

    public int getId() {
        return id;
    }

    public BombExploded setId(int id) {
        this.id = id;
        return this;
    }
}
