package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExtraDamageDecorator extends ExtraEffectDecorator {
    private static final long serialVersionUID = 793450342025388024L;
    private final int[] damageDistribution;
    private final TargetType targetType;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution, TargetType targetType) {
        this.effect = effect;
        this.description = effect.description;
        setProperties(effect.getProperties());
        this.targets = effect.targets;
        this.cost = effect.cost;
        this.damageDistribution = extraDamageDistribution;
        this.targetType = targetType;
    }

    /**
     * Method that spreads the damages of the effect to all targets.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the damage
     * distribution depends on this.
     *
     * @param request of the effect
     */
    @Override
    public void execute(EffectRequest request) {
        effect.execute(request);

        List<String> targetsUsername;
        Player shooter = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());

        switch (targetType) {
            case PLAYER:
                targetsUsername = request.getTargetPlayersUsername();
                if(targetsUsername.size() > 1 && damageDistribution.length == 1) {
                    samePlayerDamageForAllTargets(shooter, targetsUsername);
                } else {
                    distributePlayerDamage(shooter, targetsUsername);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = request.getTargetPositions();
                if(squares.size() > 1 && damageDistribution.length == 1) {
                    sameSquareDamageForAllTargets(shooter, squares);
                } else {
                    distributeSquareDamage(shooter, squares);
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(request.getTargetRoomColor());
                for (Player damaged : targetRoom) {
                    damaged.getPlayerBoard().addDamage(shooter, damageDistribution[0]);
                }
        }
    }

    private void distributePlayerDamage(Player shooter, List<String> targetsUsername) {
        for (int i = 0; i < targetsUsername.size(); ++i) {
            Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).getPlayerBoard().addDamage(shooter, damageDistribution[i]);
        }
    }

    private void sameSquareDamageForAllTargets(Player shooter, List<PlayerPosition> squares) {
        for (PlayerPosition square : squares) {
            List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(square);
            for (Player damaged : targetSquare) {
                if (shooter != damaged) {
                    damaged.getPlayerBoard().addDamage(shooter, damageDistribution[0]);
                }
            }
        }
    }

    private void distributeSquareDamage(Player shooter, List<PlayerPosition> squares) {
        for (int i = 0; i < squares.size(); ++i) {
            List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
            for (Player damaged : targetSquare) {
                if (shooter != damaged) {
                    damaged.getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                }
            }
        }
    }

    private void samePlayerDamageForAllTargets(Player shooter, List<String> targetsUsername) {
        for (String targetUsername : targetsUsername) {
            Game.getInstance().getUserPlayerByUsername(targetUsername).getPlayerBoard().addDamage(shooter, damageDistribution[0]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtraDamageDecorator that = (ExtraDamageDecorator) o;
        return Arrays.equals(damageDistribution, that.damageDistribution) &&
                targetType == that.targetType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), targetType);
        result = 31 * result + Arrays.hashCode(damageDistribution);
        return result;
    }
}
