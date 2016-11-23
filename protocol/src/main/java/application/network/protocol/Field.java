package application.network.protocol;

import java.io.Serializable;

/**
 * Diese Klasse beschreibt ein Feld innerhalb des Labyrinths.
 */
public class Field implements Serializable{

    public enum Content
    {
        EMPTY,
        WALL,
        UNDESTRUCTIBLE_WALL
    }

    private int positionX;
    private int positionY;
    private Content content;

    public int getPositionX() {
        return positionX;
    }

    public Field setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return positionY;
    }

    public Field setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }

    public Content getContent() {
        return content;
    }

    public Field setContent(Content content) {
        this.content = content;
        return this;
    }
}
