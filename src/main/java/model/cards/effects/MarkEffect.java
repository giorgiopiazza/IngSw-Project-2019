package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class MarkEffect extends Effect {

    public MarkEffect(Ammo[] cost) {
        super(cost);
    }

    /**
     * Method that executes a MarkEffect spreading marks to each corresponding TargetPlayer
     *
     * @param firingAction contains informations of how and on who the effect is executed
     * @param markDealer the Player who gives marks
     */
    @Override
    public void execute(FiringAction firingAction, Player markDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addMark(markDealer, firingAction.getMarkDistribution()[i]);
        }
    }
}
