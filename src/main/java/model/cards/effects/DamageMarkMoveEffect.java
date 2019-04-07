package model.cards.effects;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.cards.FiringAction;
import model.player.Player;

public class DamageMarkMoveEffect extends Effect {
    public DamageMarkMoveEffect(Ammo[] cost) {
        super(cost);
    }

    @Override
    public void execute(FiringAction firingAction, Player playerDealer) throws AdrenalinaException {

    }
}
