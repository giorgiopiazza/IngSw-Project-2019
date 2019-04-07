package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class DamageEffect extends Effect {

    public DamageEffect(Ammo[] cost) {
        super(cost);
    }

    @Override
    public void execute(FiringAction firingAction, Player damageDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addDamage(damageDealer, firingAction.getDamageDistribution()[i]);
        }
    }
}
