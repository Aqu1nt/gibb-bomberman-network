package application.network.protocol;

/**
 * Diese Nachricht wird verwendet um allen Clients die getroffenen Spieler mitzuteilen.
 */
public class PlayerHit {

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public PlayerHit setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }
}
