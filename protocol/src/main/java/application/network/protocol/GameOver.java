package application.network.protocol;

import application.network.api.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Nachricht wird verwendent um allen das Spielende und den Hiscore mitzuteilen.
 */
public class GameOver implements Message {

    private List<HiscoreEntry> highscore;
    private String winnerName;

    public GameOver() {
        this.highscore = new ArrayList<>();
    }

    public List<HiscoreEntry> getHighscore() {
        return highscore;
    }

    public GameOver setHighscore(List<HiscoreEntry> highscore) {
        this.highscore = highscore;
        return this;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public GameOver setWinnerName(String winnerName) {
        this.winnerName = winnerName;
        return this;
    }
}
