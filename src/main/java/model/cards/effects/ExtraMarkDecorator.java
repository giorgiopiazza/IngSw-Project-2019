package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.List;

public class ExtraMarkDecorator extends ExtraEffectDecorator {
    private final int[] markDistribution;
    private final TargetType targetType;

    public ExtraMarkDecorator(Effect effect, int[] markDistribution, TargetType targetType) {
        this.effect = effect;
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
                targetsUsername = request.getTargetPlayersUsernames();
                for (int i = 0; i < targetsUsername.size(); ++i) {
                    Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).getPlayerBoard().addMark(shooter, markDistribution[i]);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = request.getTargetPositions();
                for (int i = 0; i < squares.size(); ++i) {
                    List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                    for (Player marked : targetSquare) {
                        marked.getPlayerBoard().addMark(shooter, markDistribution[i]);
                    }
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(request.getTargetRoomColor());
                for (Player marked : targetRoom) {
                    marked.getPlayerBoard().addMark(shooter, markDistribution[0]);
                }
        }
    }
}
