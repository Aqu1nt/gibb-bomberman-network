package application.network.protocol;

import application.network.api.Message;


/**
 * Diese Nachricht wird vom Server gesendet um den Clients mitzuteilen, dass ein ein Neuer Spieler im Spiel ist.
 */
public class PlayerJoined implements Message {


    private String playerName;

    private int positionX;

    private int positionY;


    public String getPlayerName() {
        return playerName;
    }

    public PlayerJoined setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public int getPositionX() {
        return positionX;
    }

    public PlayerJoined setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return positionY;
    }

    public PlayerJoined setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }

}
