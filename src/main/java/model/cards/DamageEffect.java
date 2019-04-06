package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.cards.RemainingDamageException;
import exceptions.cards.TooManyDamageException;
import model.player.Player;

public class DamageEffect extends Effect {

    private final int damage;
    private final int[] damageDistribution;

    public DamageEffect(Ammo[] cost, Target target, int damage, int[] damageDistribution) {
        super(cost, target);
        this.damage = damage;
        this.damageDistribution = damageDistribution;
    }

    public int getDamage() {
        return this.damage;
    }

    public int[] getDamageDistribution() {
        return this.damageDistribution;
    }

    @Override
    public void execute(Target target, Player damageDealer) throws AdrenalinaException {
        int damageCounter = 0;
        for (int i = 0; i < target.getTarget().size(); ++i) {
            target.getTarget().get(i).getPlayerBoard().addDamage(damageDealer, damageDistribution[i]);
            damageCounter += damageDistribution[i];
        }
        if (damageCounter > damage) throw new TooManyDamageException(damage);
        if (damageCounter < damage) throw new RemainingDamageException(damageCounter);
    }
}
