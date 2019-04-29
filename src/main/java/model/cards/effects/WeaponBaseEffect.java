package model.cards.effects;

import enumerations.TargetType;
import model.Game;
import model.player.AmmoQuantity;
import model.player.PlayerPosition;
import utility.CommandUtility;
import utility.CommandValidator;
import utility.PropertiesValidator;

import java.util.List;
import java.util.Map;

public class WeaponBaseEffect extends Effect {
    private AmmoQuantity cost;

    public WeaponBaseEffect(AmmoQuantity cost, Map<String, String> properties, TargetType[] targets) {
        setCost(cost);
        setProperties(properties);
        setTargets(targets);
    }

    /**
     * Setter of the cost of an Effect
     *
     * @param cost the cost of the effect
     */
    public void setCost(AmmoQuantity cost) {
        this.cost = cost;
    }

    /**
     * @return the cost of the Effect
     */
    public AmmoQuantity getCost() {
        return this.cost;
    }

    @Override
    public void execute(String command) {
        // Basic Effect does nothing
    }

    @Override
    public boolean validate(String command) {
        if (getTargets().length > 1) { // This effect has subEffects
            for (TargetType targetType : getTargets()) { // Checks that every subEffect is valid
                if (!subValidate(CommandUtility.getSubCommand(command, targetType), PropertiesValidator.getSubMap(getProperties(), targetType), targetType)) {
                    return false;
                }
            }
            return true;
        } else { // Checks the effect normally
            return subValidate(command, getProperties(), getTargets()[0]);
        }
    }

    private boolean subValidate(String command, Map<String, String> properties, TargetType targetType) {
        String[] commandSplit = command.split(" ");
        PlayerPosition shooterPosition = Game.getInstance().getPlayerByID(CommandUtility.getShooterPlayerID(commandSplit)).getPosition();
        List<PlayerPosition> targetPositions = CommandUtility.getTargetPositions(commandSplit, targetType);

        // Command targets validation
        if (!CommandValidator.isTargetValid(command, properties, targetType))
            return false;

        // Player moves validation
        if (!PropertiesValidator.isMoveValid(command, properties))
            return false;

        // Simulates player movement before shooting
        if (command.contains("-y") && CommandUtility.getBoolParam(command.split(" "), "-y")) {
            shooterPosition = CommandUtility.getPositions(command.split(" "), "-m").get(0);
        }

        // Move before validation
        if (!CommandValidator.isMoveBeforeValid(command, properties)) {
            return false;
        }

        // Simulates targets movements before shooting
        if (targetType == TargetType.PLAYER && command.contains("-z") && CommandUtility.getBoolParam(command.split(" "), "-z")) {
            targetPositions = CommandUtility.getPositions(command.split(" "), "-u");
        }

        // Target distance validation
        if (!PropertiesValidator.isDistanceValid(properties, shooterPosition, targetPositions, targetType)) {
            return false;
        }

        // Target visibility validation
        if (!PropertiesValidator.isVisibilityValid(properties, shooterPosition, targetPositions)) {
            return false;
        }

        // Simulates player movement after shooting
        if (command.contains("-y") && !CommandUtility.getBoolParam(command.split(" "), "-y")) {
            shooterPosition = CommandUtility.getPositions(command.split(" "), "-m").get(0);
        }

        // Simulates targets movements after shooting
        if (targetType == TargetType.PLAYER && command.contains("-z") && !CommandUtility.getBoolParam(command.split(" "), "-z")) {
            targetPositions = CommandUtility.getPositions(command.split(" "), "-u");
        }

        // After move positioning validation
        return !(targetType == TargetType.PLAYER && !PropertiesValidator.isPositioningValid(properties, shooterPosition, targetPositions));
    }
}
