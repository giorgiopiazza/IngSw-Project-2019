package network.message;

import enumerations.MessageContent;
import model.Game;
import model.cards.PowerupCard;

public class BroadcastSpawningPowerup extends Message {
    private PowerupCard powerupCard;

    public BroadcastSpawningPowerup(PowerupCard powerupCard) {
        super(Game.GOD, null, MessageContent.DISCARD_POWERUP);

        this.powerupCard = powerupCard;
    }

    public PowerupCard getPowerupCard() {
        return this.powerupCard;
    }
}
