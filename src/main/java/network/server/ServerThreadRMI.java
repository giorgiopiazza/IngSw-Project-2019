package network.server;

import network.message.Message;

import java.net.Socket;

class ServerThreadRMI extends ServerThread {

    ServerThreadRMI(Socket socket, String username) {
        super(username);
    }

    @Override
    public void run() {

    }

    @Override
    public void sendToClient(Message message) {

    }

    @Override
    public void close(Message message) {

    }
}
