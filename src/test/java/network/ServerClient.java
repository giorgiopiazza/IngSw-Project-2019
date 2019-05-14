package network;

import model.player.PlayerPosition;
import network.client.Client;
import network.message.GameStateMessage;
import network.message.Message;
import network.message.MoveRequest;
import network.server.MultiServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ServerClient {

    @Test
    void serverTest() throws IOException, InterruptedException {
        MultiServer multiServer = new MultiServer();

        for (;;) {
            multiServer.acceptSocketClient();
            multiServer.sendToAll(new GameStateMessage());
            Thread.sleep(10);
            multiServer.sendToAll(new GameStateMessage());
            Thread.sleep(10);
            multiServer.sendToAll(new GameStateMessage());
        }
    }

    @Test
    void clientTest() throws IOException, InterruptedException {
        Client client = new Client("tose", "localhost");

        for (int i=0; i<5; i++) {
            client.sendMessage(new MoveRequest("tose", new PlayerPosition(0, 0)));
            List<Message> messages = client.receiveMessages();
            System.out.println(Arrays.toString(messages.toArray()));
            Thread.sleep(10);
        }
    }
}
