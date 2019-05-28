package network.client;

import view.cli.Cli;
import view.gui.Gui;

public class ClientMain {
    public static void main(String[] args) {
        if (args.length > 0) {
            switch (args[0].toUpperCase()) {
                case "CLI":
                    new Cli().start();
                    break;
                default:
                    new Gui().start();
            }
        } else {
            new Gui().start();
        }
    }
}
