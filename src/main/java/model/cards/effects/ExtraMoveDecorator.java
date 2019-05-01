package model.cards.effects;

import enumerations.MoveTarget;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.List;

public class ExtraMoveDecorator extends ExtraEffectDecorator {
    private final MoveTarget moveTarget;

    public ExtraMoveDecorator(Effect effect, MoveTarget moveTarget) {
        this.effect = effect;
        this.moveTarget = moveTarget;
    }

    /**
     * Method that makes every target move to the specified position in the command.
     * An effect can both move the shooting player and his targets, depending on
     * what the command is saying.
     *
     * @param request of the effect
     */
    @Override
    public void execute(EffectRequest request) {
        effect.execute(request);

        if (moveTarget == MoveTarget.PLAYER) {
            Player shooter = Game.getInstance().getPlayerByID(request.getSenderID());
            PlayerPosition shooterMovement = request.getSenderMovePosition();
            shooter.changePosition(shooterMovement.getCoordX(), shooterMovement.getCoordY());
        } else { // MoveTarget.TARGET
            List<Integer> targetsID = request.getTargetPlayersID();
            List<PlayerPosition> movingPositions = request.getTargetPlayersMovePositions();

            for (int i = 0; i < movingPositions.size(); ++i) {
                Game.getInstance().getPlayerByID(targetsID.get(i)).changePosition(
                        movingPositions.get(i).getCoordX(), movingPositions.get(i).getCoordY());
            }
        }
    }
}
