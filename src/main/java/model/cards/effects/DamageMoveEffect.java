package model.cards.effects;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import model.cards.FiringAction;
import model.player.Player;

public class DamageMoveEffect extends Effect {
    public DamageMoveEffect(Ammo[] cost) {
        super(cost);
    }

    /**
     * Method that executes a MoveEffect and a MarkEffect spreading moves and damage to each corresponding player
     *
     * @param firingAction contains informations of how and on who the effect is executed
     * @param playerDealer the Player who deals damage
     */
    @Override
    public void execute(FiringAction firingAction, Player playerDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].changePosition(
                    firingAction.getPositionDistribution()[i].getCoordX(),
                    firingAction.getPositionDistribution()[i].getCoordY());
        }

        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addDamage(playerDealer, firingAction.getDamageDistribution()[i]);
        }
    }
}
