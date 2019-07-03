package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExtraMarkDecorator extends ExtraEffectDecorator {
    private static final long serialVersionUID = 4095069933567546259L;
    private final int[] markDistribution;
    private final TargetType targetType;

    public ExtraMarkDecorator(Effect effect, int[] markDistribution, TargetType targetType) {
        this.effect = effect;
        this.description = effect.description;
        setProperties(effect.getProperties());
        this.targets = effect.targets;
        this.cost = effect.cost;
        this.markDistribution = markDistribution;
        this.targetType = targetType;
    }

    /**
     * Method that spreads the marks of the effect to the targets.
     * A target can be {@code PLAYER}, {@code SQUARE} or {@code ROOM} and the mark
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
                if(targetsUsername.size() > 1 && markDistribution.length == 1) {
                    samePlayerMarksForAllTargets(shooter, targetsUsername);
                } else {
                    distributePlayerMarks(shooter, targetsUsername);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = request.getTargetPositions();
                if(squares.size() > 1 && markDistribution.length == 1) {
                    sameSquareMarksForAllTargets(shooter, squares);
                } else {
                    distributeMarkDamage(shooter, squares);
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(request.getTargetRoomColor());
                for (Player marked : targetRoom) {
                    marked.getPlayerBoard().addMark(shooter, markDistribution[0]);
                }
        }
    }

    private void sameSquareMarksForAllTargets(Player shooter, List<PlayerPosition> squares) {
        for (PlayerPosition square : squares) {
            List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(square);
            for (Player damaged : targetSquare) {
                if (shooter != damaged) {
                    damaged.getPlayerBoard().addMark(shooter, markDistribution[0]);
                }
            }
        }
    }

    private void distributePlayerMarks(Player shooter, List<String> targetsUsername) {
        for (int i = 0; i < targetsUsername.size(); ++i) {
            Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).getPlayerBoard().addMark(shooter, markDistribution[i]);
        }
    }

    private void samePlayerMarksForAllTargets(Player shooter, List<String> targetsUsername) {
        for (String targetUsername : targetsUsername) {
            Game.getInstance().getUserPlayerByUsername(targetUsername).getPlayerBoard().addMark(shooter, markDistribution[0]);
        }
    }

    private void distributeMarkDamage(Player shooter, List<PlayerPosition> squares) {
        for (int i = 0; i < squares.size(); ++i) {
            List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
            for (Player damaged : targetSquare) {
                if (shooter != damaged) {
                    damaged.getPlayerBoard().addMark(shooter, markDistribution[i]);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtraMarkDecorator that = (ExtraMarkDecorator) o;
        return Arrays.equals(markDistribution, that.markDistribution) &&
                targetType == that.targetType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), targetType);
        result = 31 * result + Arrays.hashCode(markDistribution);
        return result;
    }
}
