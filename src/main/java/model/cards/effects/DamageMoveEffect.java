package model.cards.effects;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.cards.FiringAction;
import model.player.Player;

public class DamageMoveEffect extends Effect {
    public DamageMoveEffect(Ammo[] cost) {
        super(cost);
    }

    @Override
    public void execute(FiringAction firingAction, Player playerDealer) throws AdrenalinaException {

    }
}
