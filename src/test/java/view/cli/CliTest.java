package view.cli;

import org.junit.jupiter.api.Test;

public class CliTest {

    @Test
    void cliTest() {
        view.cli.Cli cli = new Cli();
        cli.start();
    }

}
