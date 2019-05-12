package network;

import model.GameSerialized;
import network.client.Client;
import network.server.MultiServer;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClient {

    @Test
    void serverTest() {
        MultiServer multiServer = new MultiServer();

        while (true) {
            if (!multiServer.acceptClient()) break;
        }

        multiServer.closeAll();
    }

    @Test
    void clientTest() throws InterruptedException {
        Client client = new Client("localhost");

        while (true) {
            int n = new Random().nextInt() % 100;
            client.sendMessage("gameState");
            GameSerialized gameSerialized = (GameSerialized) client.receiveObject();
            Logger.getGlobal().log(Level.INFO, gameSerialized.toString());
            if (n % 11 == 0) break;
            Thread.sleep(5000);
        }

        client.close();
    }

    @Test
    void clientTest2() throws InterruptedException {
        Client client = new Client("localhost");

        while (true) {
            int n = new Random().nextInt() % 100;
            client.sendMessage(n + "");
            Logger.getGlobal().log(Level.INFO, client.receiveMessage());
            if (n % 11 == 0) break;
            Thread.sleep(5000);
        }

        client.close();
    }

}
