package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class DamageMarkEffect extends Effect {

    public DamageMarkEffect(Ammo[] cost) {
        super(cost);
    }

    @Override
    public void execute(FiringAction firingAction, Player playerDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addDamage(playerDealer, firingAction.getDamageDistribution()[i]);
        }

        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addMark(playerDealer, firingAction.getMarkDistribution()[i]);
        }
    }
}
