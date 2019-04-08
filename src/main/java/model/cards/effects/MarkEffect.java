package model.cards.effects;

import exceptions.cards.MarkDistributionException;
import model.cards.Target;
import model.player.Player;

/*
***************** LOGICA COSTRUTTORE E SETTER DESCRITTE IN model.effects.DamageEffect *****************
 */
public class MarkEffect extends Effect {

    private int[] markDistribution;

    public MarkEffect(Target target, int[] markDistribution) throws MarkDistributionException {
        this.target = target;
        if(target.getRoom().isPresent()) {
            this.markDistribution = new int[0];
        } else {
            if(markDistribution.length != target.getTargets().length) throw new MarkDistributionException();
            this.markDistribution = markDistribution;
        }
    }

    public void setMarkDistribution(int[] markDistribution) throws MarkDistributionException {
        if(markDistribution.length != target.getTargets().length) throw new MarkDistributionException();
        this.markDistribution = markDistribution;
    }

    /**
     * Method that executes a MarkEffect spreading marks to each corresponding TargetPlayer
     *
     * @param markDealer the Player who gives marks
     */
    @Override
    public void execute(Player markDealer) {
        for (int i = 0; i < this.target.getTargets().length; ++i) {
            this.target.getTargets()[i].getPlayerBoard().addMark(markDealer, markDistribution[i]);
        }
    }
}
