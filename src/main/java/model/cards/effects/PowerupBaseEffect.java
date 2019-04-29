package model.cards.effects;

import enumerations.TargetType;
import exceptions.utility.InvalidPropertiesException;
import model.Game;
import model.player.PlayerPosition;
import utility.CommandUtility;
import utility.CommandValidator;
import utility.PropertiesValidator;

import java.util.List;
import java.util.Map;

public class PowerupBaseEffect extends Effect {
    private final int cost;     // or a boolean, in this way is more general for effects that cost more than one ammo

    public PowerupBaseEffect(Map<String, String> properties, TargetType[] targets) {
        this.cost = 0;
        setTargets(targets);
        setProperties(properties);
    }

    public PowerupBaseEffect(int generalCost, Map<String, String> properties, TargetType[] targets) {
        this.cost = generalCost;
        setTargets(targets);
        setProperties(properties);
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public void execute(String command) {
        // basic effect does nothing
    }

    @Override
    public boolean validate(String command) {
        if(getTargets().length > 1) {   // as normal weapon effects powerup effects do not have subEffects and then their target[] dimension must always be 1
            throw new InvalidPropertiesException();
        }

        String[] commandSplit = command.split(" ");
        PlayerPosition powerupUserPos = Game.getInstance().getPlayerByID(CommandUtility.getCommandUserID(commandSplit)).getPosition();
        List<PlayerPosition> targetPos = CommandUtility.getTargetPositions(commandSplit, getTargets()[0]);

        // command targets validation
        if(!CommandValidator.isTargetValid(command, getProperties(), getTargets()[0])) {
            return false;
        }

        // powerup index validation
        if(!CommandValidator.isPowerupIndexValid(command)) {
            return false;
        }

        // moves validation
        if(!PropertiesValidator.isMoveValid(command, getProperties())) {
            return false;
        }

        // visibility validation
        if(!PropertiesValidator.isVisibilityValid(getProperties(), powerupUserPos, targetPos)) {
            return false;
        }

        return true;
    }
}
