package application.network.protocol;

import application.network.api.Message;

/**
 * Diese Nachricht wird verwendet um den Loginvorgang dem Client gegenüber zu bestätigen.
 */
public class LoginSucceeded implements Message {

    private int initalPositionX;
    private int initalPositionY;

    public int getInitalPositionX() {
        return initalPositionX;
    }

    public LoginSucceeded setInitalPositionX(int initalPositionX) {
        this.initalPositionX = initalPositionX;
        return this;
    }

    public int getInitalPositionY() {
        return initalPositionY;
    }

    public LoginSucceeded setInitalPositionY(int initalPositionY) {
        this.initalPositionY = initalPositionY;
        return this;
    }
}
