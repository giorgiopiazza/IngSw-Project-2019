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
        this.description = effect.description;
        this.properties = effect.properties;
        this.targets = effect.targets;
        this.cost = effect.cost;
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
            Player shooter = Game.getInstance().getUserPlayerByUsername(request.getSenderUsername());
            PlayerPosition shooterMovement = request.getSenderMovePosition();
            shooter.changePosition(shooterMovement.getCoordX(), shooterMovement.getCoordY());
        } else { // MoveTarget.TARGET
            List<String> targetsUsername = request.getTargetPlayersUsername();
            List<PlayerPosition> movingPositions = request.getTargetPlayersMovePositions();

            for (int i = 0; i < movingPositions.size(); ++i) {
                Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).changePosition(
                        movingPositions.get(i).getCoordX(), movingPositions.get(i).getCoordY());
            }
        }
    }
}
