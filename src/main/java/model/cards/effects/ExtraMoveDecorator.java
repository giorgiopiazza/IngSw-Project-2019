package model.cards.effects;

import enumerations.MoveTarget;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import network.message.EffectRequest;

import java.util.List;
import java.util.Objects;

/**
 * Implemets the Move decoration as each movement can be performed
 */
public class ExtraMoveDecorator extends ExtraEffectDecorator {
    private static final long serialVersionUID = -1299754577436399885L;
    private final MoveTarget moveTarget;

    /**
     * Builds the Move Decoration
     *
     * @param effect to be decorated
     * @param moveTarget the target moving position to be performed
     */
    public ExtraMoveDecorator(Effect effect, MoveTarget moveTarget) {
        this.effect = effect;
        this.description = effect.description;
        setProperties(effect.getProperties());
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
            shooter.changePosition(shooterMovement.getRow(), shooterMovement.getColumn());
        } else { // MoveTarget.TARGET
            List<String> targetsUsername = request.getTargetPlayersUsername();
            List<PlayerPosition> movingPositions = request.getTargetPlayersMovePositions();

            for (int i = 0; i < movingPositions.size(); ++i) {
                Game.getInstance().getUserPlayerByUsername(targetsUsername.get(i)).changePosition(
                        movingPositions.get(i).getRow(), movingPositions.get(i).getColumn());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtraMoveDecorator that = (ExtraMoveDecorator) o;
        return moveTarget == that.moveTarget;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), moveTarget);
    }
}
