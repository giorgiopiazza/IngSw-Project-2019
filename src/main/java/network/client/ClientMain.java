package network.client;

import enumerations.MessageContent;
import network.message.Message;
import network.message.Response;
import network.server.Server;

import java.util.List;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        ClientRMI rmi = new ClientRMI("Pippo", null, Server.RMI_PORT);
        rmi.startConnection();

        ClientSocket sock = new ClientSocket("Pluto", null, Server.SOCKET_PORT);
        sock.startConnection();

        List<Message> messages;
        do {
            messages = rmi.receiveMessages();
        } while (messages.isEmpty());

        System.out.println("-- RMI --");
        for (Message msg : messages) {
            if (msg.getContent() == MessageContent.RESPONSE) {
                Response response = (Response) msg;
                System.out.println("Status: " + response.getStatus().name());
                System.out.println("Message: " + response.getMessage());
            }
        }

        do {
            messages = sock.receiveMessages();
        } while (messages.isEmpty());

        System.out.println("-- SOCKET --");
        for (Message msg : messages) {
            if (msg.getContent() == MessageContent.RESPONSE) {
                Response response = (Response) msg;
                System.out.println("Status: " + response.getStatus().name());
                System.out.println("Message: " + response.getMessage());
            }
        }
    }
}
