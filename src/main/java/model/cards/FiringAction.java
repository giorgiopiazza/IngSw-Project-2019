package model.cards;

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
    }

    public void setDamageDistribution(int[] damageDistribution) {
        if (damageDistribution.length != targets.length) {
            // TODO
        }

        this.damageDistribution = damageDistribution;
    }

    public void setMarkDistribution(int[] markDistribution) {
        if (markDistribution.length != targets.length) {
            // TODO
        }

        this.markDistribution = markDistribution;
    }

    public void setPositionDistribution(PlayerPosition[] positionDistribution) {
        if (positionDistribution.length != targets.length) {
            // TODO
        }

        this.positionDistribution = positionDistribution;
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
}
