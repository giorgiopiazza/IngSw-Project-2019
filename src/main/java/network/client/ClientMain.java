package network.client;

import network.server.Server;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        ClientRMI rmi = new ClientRMI("Pippo", null, Server.RMI_PORT);
        rmi.startConnection();

        ClientSocket sock = new ClientSocket("Pluto", null, Server.SOCKET_PORT);
        sock.startConnection();
    }
}
