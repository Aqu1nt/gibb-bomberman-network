package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird von dem Client verwendet um dem Server eine Bombenposition mitzuteilen.
 */
public class DropBomb implements Message {

    private String playerName;
    private int positionX;
    private int positionY;

    public String getPlayerName() {
        return playerName;
    }

    public DropBomb setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public int getPositionX() {
        return positionX;
    }

    public DropBomb setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return positionY;
    }

    public DropBomb setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }
}
