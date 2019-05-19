package network.message;

import enumerations.MessageContent;

public class DiscardPowerupRequest extends Message {
    // indexes must be given starting from 0 !
    private final int powerup;

    public DiscardPowerupRequest(String username, int powerup) {
        super(username, MessageContent.DISCARD_POWERUP);

        this.powerup = powerup;
    }

    public int getPowerup() {
        return powerup;
    }
}
