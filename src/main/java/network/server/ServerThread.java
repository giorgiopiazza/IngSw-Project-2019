package network.server;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

class ServerThread extends Thread {
    private static final String EXIT = "exit";
    private int id;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    ServerThread(Socket socket, int id) {
        this.id = id;
        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) { MultiServer.LOGGER.log(Level.SEVERE, e.toString()); }
    }

    @Override
    public synchronized void start() {
        super.start();
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: started", this.id);
    }

    @Override
    public void run() {
        synchronized (socket) {
            while (true) {
                String cmd;

                try {
                    cmd = in.readLine();
                } catch (IOException e) {
                    MultiServer.LOGGER.log(Level.SEVERE, e.toString());
                    cmd = EXIT;
                }

                if (cmd == null || cmd.equals(EXIT)) break;
                out.println("client: " + id + ", cmd: " + cmd);

                MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: {1}", new Object[] {id, cmd});
            }

            try {
                socket.close();
            } catch (IOException e) { MultiServer.LOGGER.log(Level.SEVERE, e.toString()); }
        }
        MultiServer.LOGGER.log(Level.INFO, "ServerThread {0}: stop", this.id);
    }

    public Socket getClient() {
        return socket;
    }
}
