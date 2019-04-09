package model.cards.effects;

import exceptions.cards.MarkDistributionException;
import model.player.Player;

public class ExtraMarkDecorator extends ExtraEffectDecorator {

    private int[] extraMarkDistribution;

    public ExtraMarkDecorator(Effect effect, int[] extraMarkDistribution) throws MarkDistributionException {
        this.effect = effect;
        if(effect.target.getRoom().isPresent()) {
            this.extraMarkDistribution = new int[0];
        } else {
            if(extraMarkDistribution.length != effect.target.getTargets().length) throw new MarkDistributionException();
            this.extraMarkDistribution = extraMarkDistribution;
        }

    }

    @Override
    public void execute(Player markDealer) {
        effect.execute(markDealer);
        for(int i = 0; i < this.effect.target.getTargets().length; ++i) {
            this.effect.target.getTargets()[i].getPlayerBoard().addMark(markDealer, extraMarkDistribution[i]);
        }
    }
}
