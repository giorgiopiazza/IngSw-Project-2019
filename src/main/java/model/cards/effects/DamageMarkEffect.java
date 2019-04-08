package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class DamageMarkEffect extends Effect {

    public DamageMarkEffect(Ammo[] cost) {
        super(cost);
    }


    /**
     * Method that executes a DamageEffect and a MarkEffect spreading damage and marks on each corresponding TargetPlayer
     *
     * @param firingAction contains informations of how and on who the effect is executed
     * @param playerDealer the Player who deals damage and gives marks
     */
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
