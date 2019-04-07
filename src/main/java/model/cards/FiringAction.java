package model.cards;

import exceptions.cards.DamageDistributionException;
import exceptions.cards.MarkDistributionException;
import exceptions.cards.PositionDistributionException;
import model.player.Player;
import model.player.PlayerPosition;

public class FiringAction {
    /**
     * FiringAction object is possessed by the effect of a weapon and changed for every attack
     */
    private final Player[] targets;
    private int[] damageDistribution;
    private int[] markDistribution;
    private PlayerPosition[] positionDistribution;

    private boolean moveBeforeDamage;

    public FiringAction(Player[] targets) {
        this.targets = targets;
        this.damageDistribution = new int[0];
        this.markDistribution = new int[0];
        this.positionDistribution = new PlayerPosition[0];
        moveBeforeDamage = false;
    }

    public void setDamageDistribution(int[] damageDistribution) throws DamageDistributionException {
        if (damageDistribution.length != targets.length) {
            throw new DamageDistributionException();
        }

        this.damageDistribution = damageDistribution;
    }

    public void setMarkDistribution(int[] markDistribution) throws MarkDistributionException {
        if (markDistribution.length != targets.length) {
            throw new MarkDistributionException();
        }

        this.markDistribution = markDistribution;
    }

    public void setPositionDistribution(PlayerPosition[] positionDistribution) throws PositionDistributionException {
        if (positionDistribution.length != targets.length) {
            throw new PositionDistributionException();
        }

        this.positionDistribution = positionDistribution;
    }

    public void setMoveBeforeDamage(boolean moveBeforeDamage) {
        this.moveBeforeDamage = moveBeforeDamage;
    }

    public Player[] getTargets() {
        return targets;
    }

    public int[] getDamageDistribution() {
        return damageDistribution;
    }

    public int[] getMarkDistribution() {
        return markDistribution;
    }

    public PlayerPosition[] getPositionDistribution() {
        return positionDistribution;
    }

    public boolean isMoveBeforeDamage() {
        return moveBeforeDamage;
    }
}
