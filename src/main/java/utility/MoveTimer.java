package utility;

import network.server.Connection;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoveTimer extends TimerTask {
    private final Connection connection;
    private final String username;

    public MoveTimer(Connection connection, String username) {
        this.connection = connection;
        this.username = username;
    }

    @Override
    public void run() {
        Logger.getGlobal().log(Level.INFO, "Player {0} disconnected, move timer ended", username);
        connection.disconnect();
    }
}
