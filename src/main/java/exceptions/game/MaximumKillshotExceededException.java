package exceptions.game;

import exceptions.AdrenalinaRuntimeException;

public class MaximumKillshotExceededException extends AdrenalinaRuntimeException {

    public MaximumKillshotExceededException() {
        super("The maximum number of skulls allowed in a game is 8");
    }

}
