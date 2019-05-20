package network.client;

import enumerations.MessageContent;
import exceptions.network.ClassAdrenalinaNotFoundException;
import network.message.ConnectionRequest;
import network.message.Message;
import network.server.MultiServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSocket extends Client {
    private Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientSocket(String username, String serverAddress) throws IOException {
        this(username, InetAddress.getByName(serverAddress));
    }


    public ClientSocket(String username, InetAddress address) throws IOException {
        super(username);

        socket = new Socket(address, MultiServer.SOCKET_PORT);
        // creazione stream di output su socket
        out = new ObjectOutputStream(socket.getOutputStream());

        out.writeObject(new ConnectionRequest(getUsername()));
        out.reset();

        // creazione stream di input da socket
        in = new ObjectInputStream(socket.getInputStream());
    }

    public InetAddress getAddress() {
        return socket.getInetAddress();
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.reset();
    }

    @Override
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

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
