package network.client;

import javafx.application.Application;
import view.cli.Cli;
import view.gui.Gui;

public class ClientMain3 {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("CLI")) {
            new Cli().start();
        } else {
            new Cli().start();
            // Application.launch(Gui.class);
        }
    }
}
