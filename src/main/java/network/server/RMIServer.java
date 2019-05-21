package network.server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    private final Server server;
    private final int port;

    public RMIServer(Server server, int port) {
        this.server = server;
        this.port = port;
    }

    void startServer() {
        try {
            RMIHandlerImplementation rmiHandler = new RMIHandlerImplementation(server);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("AdrenalineServer", rmiHandler);
        } catch (IOException | AlreadyBoundException e) {
            Server.LOGGER.severe(e.getMessage());
        }
    }
}
