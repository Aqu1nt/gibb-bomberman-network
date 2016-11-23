package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird verwendet um Spielerbewegungen in beide Richtungen zu kommunizieren.
 */
public class PlayerMoved implements Message {

    public enum Direction
    {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private Direction direction;
    private String playerName;

    public Direction getDirection() {
        return direction;
    }

    public PlayerMoved setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerMoved setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }
}
