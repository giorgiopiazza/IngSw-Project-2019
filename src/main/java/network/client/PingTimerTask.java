package network.client;

import java.util.TimerTask;

public class PingTimerTask extends TimerTask {

    private DisconnectionListener disconnectionListener;

    PingTimerTask(DisconnectionListener disconnectionListener) {
        super();
        this.disconnectionListener = disconnectionListener;
    }

    @Override
    public void run() {
        disconnectionListener.onDisconnection();
    }
}
