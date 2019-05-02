package network;

import network.client.Client;
import network.server.MultiServer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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
    void clientTest() throws IOException {
        Client client = new Client("localhost");

        client.sendMessage(new Random().nextInt() % 100 + "");
        Logger.getGlobal().log(Level.INFO,client.receiveMessage());

        client.close();
    }

}
