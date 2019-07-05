package network.message;

import enumerations.MessageContent;

/**
 * Message class for request a discard of a powerup for spawn
 */
public class DiscardPowerupRequest extends Message {
    private static final long serialVersionUID = -6885404447987158413L;

    private final int powerup;

    public DiscardPowerupRequest(String username, String token, int powerup) {
        super(username, token, MessageContent.DISCARD_POWERUP);

        this.powerup = powerup;
    }

    public int getPowerup() {
        return powerup;
    }

    @Override
    public String toString() {
        return "DiscardPowerupRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() + ", " +
                "powerup=" + powerup +
                '}';
    }
}
