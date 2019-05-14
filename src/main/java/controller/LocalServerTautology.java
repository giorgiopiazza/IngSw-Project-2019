package controller;

import view.cli.Cli;

public class LocalServerTautology {

    public static void main(String[] args) {
        /*GameManager match = new GameManager();
        match.run();*/

        Cli cli = new Cli();
        cli.start();
    }
}
