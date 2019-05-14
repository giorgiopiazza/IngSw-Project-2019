package network.server;

import network.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

abstract class ServerThread extends Thread {
    int id;
    final Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    String username;

    ServerThread(Socket socket, String username, ObjectInputStream in) {
        this.username = username;
        this.socket = socket;

        try {
            this.in = in;
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) { MultiServer.LOGGER.log(Level.SEVERE, e.toString()); }
    }

    public Socket getClient() {
        return socket;
    }

    public abstract void sendToClient(Message message);

    public String getUsername() {
        return username;
    }
}
