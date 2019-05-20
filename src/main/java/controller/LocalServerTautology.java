package controller;

import view.cli.Cli;

public class LocalServerTautology {

    public static void main(String[] args) {
        GameManager match = GameManager.getInstance();
        match.run();

        /*Cli cli = new Cli();
        cli.start();*/
    }
}
