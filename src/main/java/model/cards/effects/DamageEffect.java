package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class DamageEffect extends Effect {

    public DamageEffect(Ammo[] cost) {
        super(cost);
    }

    /**
     * Method that executes a DamageEffect spreading damage to each corresponding TargetPlayer
     *
     * @param firingAction contains informations of how and on who the effect is executed
     * @param damageDealer the Player who deals damage
     */
    @Override
    public void execute(FiringAction firingAction, Player damageDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addDamage(damageDealer, firingAction.getDamageDistribution()[i]);
        }
    }
}
