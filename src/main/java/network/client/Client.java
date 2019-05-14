package network.client;

import network.server.MultiServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private Socket socket;
    private InetAddress address;

    private BufferedReader in;
    private ObjectInputStream objReader;
    private PrintWriter out;

    public Client(String serverAddress) {
        try {
            address = InetAddress.getByName(serverAddress);
            socket = new Socket(address, MultiServer.PORT);

            createStream();
        } catch (IOException e) { Logger.getGlobal().log(Level.SEVERE, e.toString()); }
    }

    public Client(InetAddress address) {
        try {
            this.address = address;
            socket = new Socket(this.address, MultiServer.PORT);

            createStream();
        } catch (IOException e) { Logger.getGlobal().log(Level.SEVERE, e.toString()); }
    }

    private void createStream() throws IOException {
        // creazione stream di input da socket
        InputStreamReader isr = new InputStreamReader( socket.getInputStream());
        in = new BufferedReader(isr);
        // creazione stream di output su socket
        OutputStreamWriter osw = new OutputStreamWriter( socket.getOutputStream());
        BufferedWriter bw = new BufferedWriter(osw);
        out = new PrintWriter(bw, true);
        objReader = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    public Object receiveObject() {
        try {
            return objReader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    public boolean close() {
        try {
            socket.close();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return false;
        }
        return true;
    }
}
