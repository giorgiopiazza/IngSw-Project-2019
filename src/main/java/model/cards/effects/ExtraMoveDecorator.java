package model.cards.effects;

import enumerations.MoveTarget;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import utility.CommandUtility;

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
     * @param command the command that will be used to move players.
     */
    @Override
    public void execute(String command) {
        effect.execute(command);

        String[] splitCommand = command.split(" ");

        if (moveTarget == MoveTarget.PLAYER) {
            Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getCommandUserID(splitCommand));
            List<PlayerPosition> shooterMovement = CommandUtility.getPositions(splitCommand, "-m");
            shooter.changePosition(shooterMovement.get(0).getCoordX(), shooterMovement.get(0).getCoordY());
        } else { // MoveTarget.TARGET
            List<Integer> targetsID = CommandUtility.getAttributesID(splitCommand, "-t");
            List<PlayerPosition> movingPositions = CommandUtility.getPositions(splitCommand, "-u");

            for (int i = 0; i < movingPositions.size(); ++i) {
                Game.getInstance().getPlayerByID(targetsID.get(i)).changePosition(
                        movingPositions.get(i).getCoordX(), movingPositions.get(i).getCoordY());
            }
        }
    }
}
