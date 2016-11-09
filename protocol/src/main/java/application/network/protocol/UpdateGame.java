package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird verwendet um allen Clients das aktualisierte Labyrinth mitzuteilen.
 */
public class UpdateGame implements Message {

    private Maze maze;

    public Maze getMaze() {
        return maze;
    }

    public UpdateGame setMaze(Maze maze) {
        this.maze = maze;
        return this;
    }
}
