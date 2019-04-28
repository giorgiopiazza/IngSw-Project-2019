package model.cards.effects;

import enumerations.Properties;
import enumerations.TargetType;
import exceptions.utility.InvalidWeaponPropertiesException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.PlayerPosition;
import utility.CommandUtility;
import utility.CommandValidator;
import utility.PropertiesValidator;

import java.util.List;
import java.util.Map;

public class WeaponBaseEffect extends Effect {
    // TODO we can add a description of the effect to give a better understanding of the weapon while playing with CLI
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

        if (!CommandValidator.isTargetValid(command, properties, targetType))
            return false;

        if (!PropertiesValidator.isMoveValid(command, properties))
            return false;

        // Simulates player movement before shooting
        if (command.contains("-y") && CommandUtility.getBoolParam(command.split(" "), "-y")) {
            shooterPosition = CommandUtility.getPositions(command.split(" "), "-m").get(0);
        }

        // TODO Fatto di fretta, si può migliorare
        if (properties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) && command.contains("-z") &&
                (Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey())) &&
                        !CommandUtility.getBoolParam(command.split(" "), "-z")) &&
                (!Boolean.parseBoolean(properties.get(Properties.MOVE_TARGET_BEFORE.getJKey())) &&
                        CommandUtility.getBoolParam(command.split(" "), "-z"))
                || properties.containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) && !command.contains("-z")) {
            return false;
        }

        // Simulates targets movements before shooting
        if (targetType == TargetType.PLAYER && command.contains("-z") && CommandUtility.getBoolParam(command.split(" "), "-z")) {
            targetPositions = CommandUtility.getPositions(command.split(" "), "-u");
        }

        if (!PropertiesValidator.isDistanceValid(properties, shooterPosition, targetPositions, targetType)) {
            return false;
        }

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

        if (targetType == TargetType.PLAYER && !PropertiesValidator.isPositioningValid(properties, shooterPosition, targetPositions)) {
            return false;
        }

        return true;
    }
}
