package network.message;

import enumerations.MessageContent;

public class DiscardPowerupRequest extends Message {
    // indexes must be given starting from 0 !
    private final int powerup;

    public DiscardPowerupRequest(String username, String token, int powerup) {
        super(username, token, MessageContent.DISCARD_POWERUP);

        this.powerup = powerup;
    }

    public int getPowerup() {
        return powerup;
    }
}
