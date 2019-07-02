package network.client;

import java.util.TimerTask;

public class PingTimerTask extends TimerTask {

    private Client client;

    public PingTimerTask(Client client) {
        super();
        this.client = client;
    }

    @Override
    public void run() {
        client.disconnected();
    }
}
