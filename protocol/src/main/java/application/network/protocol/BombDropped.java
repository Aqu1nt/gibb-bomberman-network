package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird von dem Server verwendet um eine neue Bombenposition allen Clients mitzuteilen.
 */
public class BombDropped implements Message {

    private int id;
    private int positionX;
    private int positionY;

    public int getId() {
        return id;
    }

    public BombDropped setId(int id) {
        this.id = id;
        return this;
    }

    public int getPositionX() {
        return positionX;
    }

    public BombDropped setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return positionY;
    }

    public BombDropped setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }
}
