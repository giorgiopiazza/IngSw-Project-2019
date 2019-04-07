package model.cards.effects;

import enumerations.Ammo;
import model.cards.FiringAction;
import model.player.Player;

public class MarkEffect extends Effect {

    public MarkEffect(Ammo[] cost) {
        super(cost);
    }

    @Override
    public void execute(FiringAction firingAction, Player markDealer) {
        for (int i = 0; i < firingAction.getTargets().length; ++i) {
            firingAction.getTargets()[i].getPlayerBoard().addMark(markDealer, firingAction.getMarkDistribution()[i]);
        }
    }
}
