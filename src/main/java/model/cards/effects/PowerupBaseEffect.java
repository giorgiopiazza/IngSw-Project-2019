package model.cards.effects;

import enumerations.Properties;
import enumerations.TargetType;
import exceptions.utility.InvalidPropertiesException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.PlayerPosition;
import network.message.EffectRequest;
import network.message.PowerupRequest;

import java.util.List;
import java.util.Map;

import static model.cards.effects.EffectValidator.teleporterValidator;

/**
 * Implements the Class from which each {@link model.cards.PowerupCard Powerups'} effect must be decorated
 */
public class PowerupBaseEffect extends Effect {

    private static final long serialVersionUID = -2807643151080169972L;

    public PowerupBaseEffect(Map<String, String> properties, TargetType[] targets, String description) {
        this.cost = new AmmoQuantity();
        this.targets = targets;
        setProperties(properties);
        this.description = description;
    }

    public PowerupBaseEffect(AmmoQuantity cost, Map<String, String> properties, TargetType[] targets, String description) {
        this.cost = cost;
        this.targets = targets;
        setProperties(properties);
        this.description = description;
    }

    @Override
    public void execute(EffectRequest request) {
        // basic effect does nothing
    }

    @Override
    public boolean validate(EffectRequest request) {
        PowerupRequest powerupRequest = (PowerupRequest) request;

        if(getProperties().containsKey(Properties.TP.getJKey())) {
            return teleporterValidator(powerupRequest);
        }

        if (getTargets().length > 1) {   // as normal weapon effects powerup effects do not have subEffects and then their target[] dimension must always be 1
            throw new InvalidPropertiesException();
        }

        PlayerPosition powerupUserPos = Game.getInstance().getUserPlayerByUsername(powerupRequest.getSenderUsername()).getPosition();
        List<PlayerPosition> targetPos = EffectValidator.getTargetPositions(powerupRequest, getTargets()[0]);

        // command targets validation
        if (!EffectValidator.isTargetValid(powerupRequest, getProperties(), getTargets()[0])) {
            return false;
        }

        // powerup index validation
        if (!EffectValidator.isPowerupIndexValid(powerupRequest)) {
            return false;
        }

        // moves validation
        if (!EffectValidator.isMoveValid(powerupRequest, getProperties())) {
            return false;
        }

        // visibility validation
        return EffectValidator.isVisibilityValid(getProperties(), powerupUserPos, targetPos);
    }
}
