package application.network.protocol;

import application.network.api.Message;


/**
 * Diese Nachricht wird verwendet um allen Clients die getroffenen Spieler mitzuteilen.
 */
public class PlayerHit implements Message {

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public PlayerHit setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }
}
