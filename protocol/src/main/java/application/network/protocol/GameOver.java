package application.network.protocol;

import application.network.api.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Nachricht wird verwendent um allen das Spielende und den Hiscore mitzuteilen.
 */
public class GameOver implements Message {

    private List<HiscoreEntry> hiscore;
    private String winnerName;

    public GameOver() {
        this.hiscore = new ArrayList<>();
    }

    public List<HiscoreEntry> getHiscore() {
        return hiscore;
    }

    public GameOver setHiscore(List<HiscoreEntry> hiscore) {
        this.hiscore = hiscore;
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
