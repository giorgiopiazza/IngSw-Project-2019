package network.message;

import enumerations.MessageContent;
import model.Game;
import model.cards.PowerupCard;
import utility.GameCostants;

public class BroadcastSpawningPowerup extends Message {
    private static final long serialVersionUID = 8037294570203660769L;

    private final PowerupCard powerupCard;

    public BroadcastSpawningPowerup(PowerupCard powerupCard) {
        super(GameCostants.GOD_NAME, null, MessageContent.DISCARD_POWERUP);

        this.powerupCard = powerupCard;
    }

    public PowerupCard getPowerupCard() {
        return this.powerupCard;
    }

    @Override
    public String toString() {
        return "BroadcastSpawningPowerup{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() + ", " +
                "powerupCard=" + powerupCard +
                '}';
    }
}
