package model.cards.effects;

import enumerations.Properties;
import enumerations.TargetType;
import model.player.AmmoQuantity;
import utility.CommandValidator;
import utility.PropertiesValidator;

import java.util.Arrays;
import java.util.LinkedHashMap;
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
    public void setCost(AmmoQuantity cost) { this.cost = cost; }

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

        // subEffects validation
        if(getTargets().length > 1) {
            Map<String, String> mapCopy = new LinkedHashMap<>(getProperties());
            TargetType targetCopy[] = new TargetType[getTargets().length];
            System.arraycopy(getTargets(), 0, targetCopy, 0, getTargets().length);

            for(int i = 0; i < getTargets().length; ++i) {
                PropertiesValidator.setTempMap(getProperties(), getTargets()[i]);
                setTargets(Arrays.copyOf(getTargets(), getTargets().length - 1));

                if(!validate(command)) {
                    setProperties(mapCopy);
                    setTargets(targetCopy);
                    return false;
                }

                setProperties(mapCopy);
                setTargets(targetCopy);
            }
            return true;
        }

        // target and command validation
        if(!CommandValidator.targetValidate(command, getTargets()[0])) {
            return false;
        }

        // number of target validation
        if(getProperties().containsKey(Properties.TARGET_NUM.getJKey())) {
            int exactNumber = Integer.parseInt(getProperties().get(Properties.TARGET_NUM.getJKey()));
            if(!CommandValidator.targetNum(command, getTargets()[0], exactNumber, true)) {
                return false;
            }
        }

        if(getProperties().containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            int number = Integer.parseInt(getProperties().get(Properties.MAX_TARGET_NUM.getJKey()));
            if(!CommandValidator.targetNum(command, getTargets()[0], number, false)) {
                return false;
            }
        }

        // distance validation
        if(getProperties().containsKey(Properties.DISTANCE.getJKey())) {
            int exactDistance = Integer.parseInt(getProperties().get(Properties.DISTANCE.getJKey()));
            if(!CommandValidator.areFar(command, getTargets()[0], exactDistance, true)) {
                return false;
            }
        }

        if(getProperties().containsKey(Properties.MIN_DISTANCE.getJKey())) {
            int distance = Integer.parseInt(getProperties().get(Properties.MIN_DISTANCE.getJKey()));
            if(!CommandValidator.areFar(command, getTargets()[0], distance, false)) {
                return false;
            }
        }


        return true;
    }
}
