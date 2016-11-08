package application.network.protocol;

import java.io.Serializable;

/**
 * Created by nschaefli on 11/8/16.
 */
public class HiscoreEntry implements Serializable{

    private String playerName;
    private int score;

    public String getPlayerName() {
        return playerName;
    }

    public HiscoreEntry setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public int getScore() {
        return score;
    }

    public HiscoreEntry setScore(int score) {
        this.score = score;
        return this;
    }
}
