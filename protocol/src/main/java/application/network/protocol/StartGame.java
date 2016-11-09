package application.network.protocol;

import application.network.api.Message;

/**
 * Nachricht wird verwendet um den Spielstart den Clients mitzutailen.
 */
public class StartGame implements Message{

    private Maze maze;

    public Maze getMaze() {
        return maze;
    }

    public StartGame setMaze(Maze maze) {
        this.maze = maze;
        return this;
    }
}
