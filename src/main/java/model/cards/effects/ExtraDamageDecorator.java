package model.cards.effects;

import exceptions.cards.DamageDistributionException;
import model.player.Player;

public class ExtraDamageDecorator extends ExtraEffectDecorator {

    private int[] extraDamageDistribution;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution) throws DamageDistributionException{
        this.effect = effect;
        if(effect.target.getRoom().isPresent()) {
            this.extraDamageDistribution = new int[0];
        } else {
            if(extraDamageDistribution.length != effect.target.getTargets().length) throw new DamageDistributionException();
            this.extraDamageDistribution = extraDamageDistribution;
        }

    }

    @Override
    public void execute(Player damageDealer) {
        effect.execute(damageDealer);
        for(int i = 0; i < this.effect.target.getTargets().length; ++i) {
            this.effect.target.getTargets()[i].getPlayerBoard().addDamage(damageDealer, extraDamageDistribution[i]);
        }
    }
}
