package model.cards.effects;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.cards.RemainingDamageException;
import exceptions.cards.RemainingMarksException;
import exceptions.cards.TooManyDamageException;
import exceptions.cards.TooManyMarksException;
import model.cards.Target;
import model.player.Player;

public class DamageMarkEffect extends Effect {

    private final int damage;
    private final int marks;
    private final int[] damageDistribution;
    private final int[] marksDistribution;

    public DamageMarkEffect(Ammo[] cost, Target target, int damage, int marks,
                            int[] damageDistribution, int[] marksDistribution) {

        super(cost, target);
        this.damage = damage;
        this.marks = marks;
        this.damageDistribution = damageDistribution;
        this.marksDistribution = marksDistribution;
    }

    public int getDamage() {
        return damage;
    }

    public int getMarks() {
        return marks;
    }

    public int[] getDamageDistribution() {
        return damageDistribution;
    }

    public int[] getMarksDistribution() {
        return marksDistribution;
    }

    @Override
    public void execute(Target target, Player playerDealer) throws AdrenalinaException {
        int damageCounter = 0;
        int marksCounter = 0;
        for (int i = 0; i < target.getTarget().size(); ++i) {
            target.getTarget().get(i).getPlayerBoard().addDamage(playerDealer, damageDistribution[i]);
            target.getTarget().get(i).getPlayerBoard().addMark(playerDealer, marksDistribution[i]);
            damageCounter += damageDistribution[i];
            marksCounter += marksDistribution[i];
        }
        if (damageCounter > damage) throw new TooManyDamageException(damage);
        if (damageCounter < damage) throw new RemainingDamageException(damageCounter);
        if (marksCounter > marks) throw new TooManyMarksException(marks);
        if (marksCounter < marks) throw new RemainingMarksException(marksCounter);
    }
}
