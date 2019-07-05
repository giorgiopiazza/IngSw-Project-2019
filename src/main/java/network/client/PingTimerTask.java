package network.client;

import java.util.TimerTask;

/**
 * This class is a superclass of {@link TimerTask} is executed after {@link Client#DISCONNECTION_TIME} milliseconds which is scheduled by a Timer,
 * once called notifies the client via a call to {@link DisconnectionListener#onDisconnection}
 */
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
