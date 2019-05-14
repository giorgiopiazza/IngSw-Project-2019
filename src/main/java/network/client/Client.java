package network.client;

import enumerations.MessageContent;
import exceptions.network.ClassAdrenalinaNotFoundException;
import network.message.ConnectionRequest;
import network.message.Message;
import network.server.MultiServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket socket;
    private InetAddress address;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username;

    public Client(String username, String serverAddress) throws IOException {
        this(username, InetAddress.getByName(serverAddress));
    }

    public Client(String username, InetAddress address) throws IOException {
        this.address = address;
        this.username = username;

        socket = new Socket(this.address, MultiServer.SOCKET_PORT);
        // creazione stream di output su socket
        out = new ObjectOutputStream(socket.getOutputStream());

        out.writeObject(new ConnectionRequest(this.username));
        out.reset();

        // creazione stream di input da socket
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void createStream() throws IOException {
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.reset();
    }

    public List<Message> receiveMessages() throws IOException {
        List<Message> messageList = new ArrayList<>();
        Message current;

        do {
            try {
                current = (Message) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new ClassAdrenalinaNotFoundException();
            }
            messageList.add(current);
        } while (current.getContent() != MessageContent.RESPONSE);

        return messageList;
    }

    public void close() throws IOException {
        socket.close();
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getAddress() {
        return address;
    }


}
