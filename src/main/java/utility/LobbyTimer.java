package utility;

import java.util.TimerTask;

public class LobbyTimer extends TimerTask {
    private TimerRunListener timerRunListener;

    public LobbyTimer(TimerRunListener timerRunListener) {
        this.timerRunListener = timerRunListener;
    }

    @Override
    public void run() {
        timerRunListener.onTimerRun();
    }
}
