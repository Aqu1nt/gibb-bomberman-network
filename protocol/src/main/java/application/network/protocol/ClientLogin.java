package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird verwendet um einem Spiel beizutreten.
 */
public class ClientLogin implements Message{
    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public ClientLogin setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }
}
