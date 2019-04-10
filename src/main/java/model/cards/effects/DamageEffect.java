package model.cards.effects;

import exceptions.cards.DamageDistributionException;
import model.cards.Target;
import model.player.Player;

public class DamageEffect extends Effect {

    private int[] damageDistribution;

    public DamageEffect(Target target, int[] damageDistribution) throws  DamageDistributionException{
        this.target = target;
        if(target.getRoom().isPresent()) {
            this.damageDistribution = new int[0];   // if the target is a room I do not know when creating the effect his damageDistribution size
        } else {
            if(damageDistribution.length != target.getTargets().length) throw new DamageDistributionException();
            this.damageDistribution = damageDistribution;   // else I always know damageDistribution dimension
        }
    }

    public void setDamageDistribution(int[] damageDistribution) throws DamageDistributionException {
        if(damageDistribution.length != target.getTargets().length) throw new DamageDistributionException();
        this.damageDistribution = damageDistribution;   // to set the damageDistribution when the target is a room
    }

    /**
     * Method that executes a DamageEffect spreading damage to each corresponding TargetPlayer
     *
     * @param damageDealer the Player who deals damage
     */
    @Override
    public void execute(Player damageDealer) {
        for (int i = 0; i < this.target.getTargets().length; ++i) {
            this.target.getTargets()[i].getPlayerBoard().addDamage(damageDealer, damageDistribution[i]);
        }
    }
}
