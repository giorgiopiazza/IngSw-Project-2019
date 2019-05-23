package network.client;

import enumerations.MessageContent;
import exceptions.network.ClassAdrenalinaNotFoundException;
import network.message.ConnectionRequest;
import network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSocket extends Client {

    private transient Socket socket;

    private transient ObjectInputStream in;
    private transient ObjectOutputStream out;

    public ClientSocket(String username, String address, int port) throws IOException {
        super(username, address, port);
    }

    @Override
    public void startConnection() throws IOException {
        socket = new Socket(getAddress(), getPort());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        sendMessage(new ConnectionRequest(getUsername()));
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        if (out != null) {
            out.writeObject(message);
            out.reset();
        }
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

    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }

        in = null;
        out = null;
    }
}
