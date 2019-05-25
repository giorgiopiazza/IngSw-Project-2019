package network.client;

import view.cli.Cli;

public class ClientMain {
    public static void main(String[] args) {
        if (args.length > 0) {
            switch (args[0].toUpperCase()) {
                case "CLI":
                    new Cli().start();
                    break;
                default:
                    // Start GUI
            }
        } else {
            // Start GUI
        }
    }
}
