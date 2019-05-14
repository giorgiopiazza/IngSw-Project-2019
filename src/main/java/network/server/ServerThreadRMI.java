package network.server;

import network.message.Message;

import java.io.IOException;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;

class ServerThreadRMI extends ServerThread {

    ServerThreadRMI(Socket socket, String username) {
        super(socket, username, null);
    }

    @Override
    public void sendToClient(Message message) {

    }
}
