package model.cards.effects;

import enumerations.TargetType;
import exceptions.utility.InvalidPropertiesException;
import model.Game;
import model.player.PlayerPosition;
import network.message.EffectRequest;
import network.message.PowerupRequest;
import utility.EffectValidator;

import java.util.List;
import java.util.Map;

public class PowerupBaseEffect extends Effect {
    private final boolean cost;

    public PowerupBaseEffect(Map<String, String> properties, TargetType[] targets) {
        this.cost = false;
        setTargets(targets);
        setProperties(properties);
    }

    public PowerupBaseEffect(boolean cost, Map<String, String> properties, TargetType[] targets) {
        this.cost = cost;
        setTargets(targets);
        setProperties(properties);
    }

    public boolean hasCost() {
        return this.cost;
    }

    @Override
    public void execute(EffectRequest request) {
        // basic effect does nothing
    }

    @Override
    public boolean validate(EffectRequest request) {
        if(getTargets().length > 1) {   // as normal weapon effects powerup effects do not have subEffects and then their target[] dimension must always be 1
            throw new InvalidPropertiesException();
        }

        PowerupRequest powerupRequest = (PowerupRequest) request;

        PlayerPosition powerupUserPos = Game.getInstance().getPlayerByID(powerupRequest.getSenderID()).getPosition();
        List<PlayerPosition> targetPos = EffectValidator.getTargetPositions(powerupRequest, getTargets()[0]);

        // command targets validation
        if(!EffectValidator.isTargetValid(powerupRequest, getProperties(), getTargets()[0])) {
            return false;
        }

        // powerup index validation
        if(!EffectValidator.isPowerupIndexValid(powerupRequest)) {
            return false;
        }

        // moves validation
        if(!EffectValidator.isMoveValid(powerupRequest, getProperties())) {
            return false;
        }

        // visibility validation
        return EffectValidator.isVisibilityValid(getProperties(), powerupUserPos, targetPos);
    }
}
