package network.server;

import network.message.Message;

import java.io.IOException;

public interface Session {
    boolean isConnected();
    void sendMessage(Message message) throws IOException;
    void disconnect();
    void ping();
}
