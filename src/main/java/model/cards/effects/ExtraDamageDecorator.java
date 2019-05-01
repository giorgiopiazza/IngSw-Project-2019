package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.List;

public class ExtraDamageDecorator extends ExtraEffectDecorator {
    private final int[] damageDistribution;
    private final TargetType targetType;

    public ExtraDamageDecorator(Effect effect, int[] extraDamageDistribution, TargetType targetType) {
        this.effect = effect;
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

        List<Integer> targetsID;
        Player shooter = Game.getInstance().getPlayerByID(request.getSenderID());

        switch (targetType) {
            case PLAYER:
                targetsID = request.getTargetPlayersID();
                for (int i = 0; i < targetsID.size(); ++i) {
                    Game.getInstance().getPlayerByID(targetsID.get(i)).getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                }
                break;
            case SQUARE:
                List<PlayerPosition> squares = request.getTargetPositions();
                for (int i = 0; i < squares.size(); ++i) {
                    List<Player> targetSquare = Game.getInstance().getGameMap().getPlayersInSquare(squares.get(i));
                    for (Player damaged : targetSquare) {
                        damaged.getPlayerBoard().addDamage(shooter, damageDistribution[i]);
                    }
                }
                break;
            default:
                List<Player> targetRoom = Game.getInstance().getGameMap().getPlayersInRoom(request.getTargetRoomColor());
                for (Player damaged : targetRoom) {
                    damaged.getPlayerBoard().addDamage(shooter, damageDistribution[0]);
                }
        }
    }
}
